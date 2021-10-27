package org.heaphop;

import org.javatuples.Triplet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.util.*;

public interface Visualizer {
    default void getState() {
        Queue<Triplet<Visualizer, Integer, String>> queue = new LinkedList<>();
        queue.add(new Triplet<>(this, -1, ""));
        JSONArray dataToBeShared = new JSONArray();
        while (!queue.isEmpty()) {
            JSONObject fields = new JSONObject();
            Triplet<Visualizer, Integer, String> currentElement = queue.poll();
            Visualizer currentVisualizer = currentElement.getValue0();
            for (var field : currentVisualizer.getClass().getDeclaredFields()) {
                boolean wasAccessible = field.canAccess(currentVisualizer);
                field.setAccessible(true);
                Object value = null;
                String fieldName = "";
                try {
                    value = field.get(currentVisualizer);
                    fieldName = field.getName();
                    fields.put(fieldName,extractValueStringEncoding(value));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                field.setAccessible(wasAccessible);
                if (Visualizer.class.isAssignableFrom(field.getType())) {
                    if (value != null) {
                        queue.add(new Triplet<>((Visualizer) value, dataToBeShared.size(), fieldName));
                    }
                }
            }
            dataToBeShared.add(Map.ofEntries(
                    Map.entry("fields", fields),
                    Map.entry("parent", currentElement.getValue1()),
                    Map.entry("fromField", currentElement.getValue2())
            ));
        }
        System.out.println(dataToBeShared.toString());
    }

    default String extractValueStringEncoding(Object value){
        if (value == null) return "null";
        if (value.getClass().isArray()){
            Class dataType = value.getClass().getComponentType();
            String valuesToString = "";
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                Object element = Array.get(value,i);
                valuesToString+= (element == null ? "null" : element.toString());
                if(i < length-1) valuesToString+=',';
            }
            return String.format("%s[]{%s}",dataType.getName(),valuesToString);
        }
        if (Iterable.class.isAssignableFrom(value.getClass())) {
            String type = "?";
            for (Object item : Iterable.class.cast(value)) {
                if(item != null) type = item.getClass().getSimpleName();
                if(!"?".equals(type)) break;
            }
            return String.format("%s<%s>%s",value.getClass().getSimpleName(), type,value);
        }
        if (Map.class.isAssignableFrom(value.getClass())) {
            String type = "?";
            for (Object pair : Iterable.class.cast(((Map<?, ?>) value).entrySet())) {
                if(pair != null) type = Map.Entry.class.cast(pair).getKey().getClass().getSimpleName() + "," + Map.Entry.class.cast(pair).getValue().getClass().getSimpleName();
                if(!"?".equals(type)) break;
            }
            return String.format("%s<%s>%s}",value.getClass().getSimpleName(), type,value);
        }
        return value.toString();
    }
}
