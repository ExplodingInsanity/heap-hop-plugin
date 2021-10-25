package org.heaphop;

import kotlin.annotation.Target;
import org.javatuples.Triplet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.annotation.ElementType;
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
                boolean isAccessible = field.canAccess(currentVisualizer);
                field.setAccessible(true);
                Object value = null;
                String fieldName = "";
                try {
                    value = field.get(currentVisualizer);
                    fieldName = field.getName();
                    if (value == null) {
                        fields.put(fieldName, "null");
                    } else {
                        fields.put(fieldName, value.toString());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                field.setAccessible(isAccessible);
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
}
