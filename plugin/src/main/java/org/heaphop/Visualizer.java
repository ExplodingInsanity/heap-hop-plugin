package org.heaphop;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.util.*;

public interface Visualizer {
    default JSONObject getState() {
        return encodeVisualizer(this);
    }

    HashSet<VisualizerEdge> visitedEdges = new HashSet<>();
    HashSet<Integer> visitedVisualizers = new HashSet<>();

    default JSONObject encodeVisualizer(Visualizer visualizerObject) {
        JSONObject fieldsJson = new JSONObject();
        fieldsJson.put("type", "visualizer");
        if (visualizerObject == null) return fieldsJson;
        int selfHashcode = visualizerObject.hashCode();
        fieldsJson.put("id",selfHashcode);
        fieldsJson.put("duplicate",visitedVisualizers.contains(selfHashcode));
//        System.out.println("duplicate:"+fieldsJson.get("duplicate"));
//        System.out.println("id:"+fieldsJson.get("id"));
//        System.out.println();
        visitedVisualizers.add(selfHashcode);
        for (var field : visualizerObject.getClass().getDeclaredFields()) {
            boolean wasAccessible = field.canAccess(visualizerObject);
            field.setAccessible(true);
            Object value;
            String fieldName;
            try {
                value = field.get(visualizerObject);
                fieldName = field.getName();
                // daca e visualizer verificam pt legaturi circulare
                if(value != null && Visualizer.class.isAssignableFrom(value.getClass())){
                    VisualizerEdge edge = new VisualizerEdge(visualizerObject,(Visualizer) value, fieldName);
                    if(visitedEdges.contains(edge)) {
                        continue;
                    }
                    visitedEdges.add(edge);
                }
                fieldsJson.put(fieldName, encodeField(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            field.setAccessible(wasAccessible);
        }
        return fieldsJson;
    }
    default JSONObject encodeField(Object value) {
        JSONObject jsonObject = new JSONObject();
        if (value == null) return jsonObject;
        if (value.getClass().isArray()) {
            JSONArray array = new JSONArray();
            jsonObject.put("type", "list");
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                Object element = Array.get(value, i);
                array.add(encodeField(element));
            }
            jsonObject.put("value", array);
        } else if (Iterable.class.isAssignableFrom(value.getClass())) {
            JSONArray array = new JSONArray();
            for (Object item : (Iterable) value) {
                array.add(encodeField(item));
            }
            jsonObject.put("type", "list");
            jsonObject.put("value", array);
            return jsonObject;
        } else if (Map.class.isAssignableFrom(value.getClass())) {
            JSONArray array = new JSONArray();
            for (Object pair : ((Map<?, ?>) value).entrySet()) {
                JSONObject keyValuePair = new JSONObject();
                keyValuePair.put("key", encodeField(((Map.Entry) pair).getKey()));
                keyValuePair.put("value", encodeField(((Map.Entry) pair).getValue()));
                array.add(keyValuePair);
            }
            jsonObject.put("type", "dictionary");
            jsonObject.put("value", array);
            return jsonObject;
        } else if (Visualizer.class.isAssignableFrom(value.getClass())) {
            jsonObject.put("type", "visualizer");
            jsonObject.put("value", encodeVisualizer((Visualizer) value));
            return jsonObject;
        } else {
            jsonObject.put("type", "atom");
            jsonObject.put("value", value);
        }
        return jsonObject;
    }
}
