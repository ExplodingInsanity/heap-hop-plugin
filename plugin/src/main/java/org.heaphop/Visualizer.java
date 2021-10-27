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
                    fields.put(fieldName, extractValueStringEncoding(value));
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
        System.out.println(dataToBeShared);
    }

    default JSONObject extractValueStringEncoding(Object value) {
        if (value == null) return new JSONObject();
        JSONObject jsonObject = new JSONObject();
        if (value.getClass().isArray()) {
            JSONArray array = new JSONArray();
            jsonObject.put("type", "list");
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                Object element = Array.get(value, i);
                array.add(extractValueStringEncoding(element));
            }
            jsonObject.put("value", array);
            return jsonObject;
        }
        if (Iterable.class.isAssignableFrom(value.getClass())) {
            JSONArray array = new JSONArray();
            for (Object item : (Iterable) value) {
                array.add(extractValueStringEncoding(item));
            }
            jsonObject.put("type", "list");
            jsonObject.put("value", array);
            return jsonObject;
        }
        if (Map.class.isAssignableFrom(value.getClass())) {
            JSONArray array = new JSONArray();
            for (Object pair : ((Map<?, ?>) value).entrySet()) {
                JSONObject keyValuePair = new JSONObject();
                keyValuePair.put("key", extractValueStringEncoding(((Map.Entry) pair).getKey()));
                keyValuePair.put("value", extractValueStringEncoding(((Map.Entry) pair).getValue()));
                array.add(keyValuePair);
            }
            jsonObject.put("type", "dictionary");
            jsonObject.put("value", array);
            return jsonObject;
        }
        if (Visualizer.class.isAssignableFrom(value.getClass())) {
            jsonObject.put("type", "visualizer");
            jsonObject.put("value", value);
            return jsonObject;
        }
        jsonObject.put("type", "atom");
        jsonObject.put("value", value);
        return jsonObject;
    }
}
