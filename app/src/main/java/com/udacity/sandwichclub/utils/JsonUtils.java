package com.udacity.sandwichclub.utils;

import android.util.Log;

import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Arrays;

import com.udacity.sandwichclub.model.Sandwich;

public class JsonUtils {

    /**
     * Parses a sandwich-specific json string into a Sandwich instance, by
     *
     *
     * @param json : String
     * @return sandwich : Sandwich
     */
    public static Sandwich parseSandwichJson(String json) {
        return integrateInto(new Sandwich(), intoListOfAttributesStrings(cleanup(json)));
    }

    /**
     * Removes all whitespaces "\s", which includes tabs"\t", newlines"\n", from the json string.
     * It also removes the all-enclosing-curly-brackets combined with the flattening of the
     * sub object"name" into the main object, meaning that its attributes are now part of the main.
     * (mainName, alsoKnownAs)
     *
     * @param json : String
     * @return  : String
     */
    private static String cleanup(String json) {
        return json
                .replaceAll("\\s*\\{\\s*\"name\"\\s*:\\s*\\{", "")
                .replaceAll("\\s*\\}\\s*", "")
                .replaceAll("\"\\s*:\\s*\"", "\":\"")
                .replaceAll("\"\\s*:\\s*\\[\\s*", "\":\\[")
                .replaceAll("\"\\s*,\\s*\"", "\",\"")
                .replaceAll("\\s*\\]\\s*", "\\]")
                .replaceAll("\\\\\"", "\"")
                ;
    }

    /**
     * Performing splitting at every comma ","
     * gives rise to 3 x distinct cases (1 x true, 2 x false/positives).
     * (1:true)   commas separating individual NAME/VALUE-pairs from each other.
     * (2:false)  commas used in an array of Strings constituting one possible VALUE instance
     * (3:false)  commas within a single String constituting the other possible VALUE instance
     * Note: The strings defining the sandwich fieldnames are known and do not contain any commas.
     * The task is now to undo the false/positives
     *
     * @param json : String
     * @return list : List<String>
     */
    private static List<String> intoListOfAttributesStrings(String json) {

        List<String> list =  new LinkedList<>(Arrays.asList(json.split(",")));

        ListIterator<String> iter = list.listIterator();
        while (iter.hasNext()) {

            /* Getting into position */
            String last = iter.next();
            if (!iter.hasNext()) break;
            String curr = iter.next();
            iter.previous();

            boolean cond = curr.contains("\":\"") || curr.contains("\":[");
            if (!cond) /* Equivalent case identifying the false/positives */ {

                iter.remove();
                iter.previous();
                iter.set(last + "," + curr);
            }
        }
        return list;
    }

    /**
     * Integrates the list of attributes into a given Sandwich,
     * having one NAME/VALUE pair per single string
     *
     * @param sandwich  (target of integration)
     * @param list  (... of Strings, each representing a single NAME/VALUE pair
     *              as to say a single attributes)
     * @return sandwich
     */
    private static Sandwich integrateInto(Sandwich sandwich, List<String> list) {

        ListIterator iter = list.listIterator();
        while (list.size() > 0) {

            String curr = iter.next().toString();
            iter.remove();

            if (curr.matches("\"\\w*\":\".*\"")) /* case: value type is a single string */{

                String[] _pair = curr  .substring(1, curr.length() - 1)   .split("\":\"", 2);
                integrate(sandwich, _pair[0], _pair[1]);

            } else if (curr.matches("\"\\w*\":\\[\".*\"\\]")) /* case: value type is a string array */{

                String[] _pair = curr  .substring(1, curr.length() - 2)   .split("\":\\[\"" , 2);
                integrate(sandwich, _pair[0], new LinkedList<String>(Arrays.asList(_pair[1].split("\",\""))));

            } else Log.d("JsonUtil::integrateInto   ",
                    "ERROR: value type neither String nor List<String>:  " + curr );
        }
        return sandwich;
    }

    /**
     * Integrates a single NAME/VALUE pair into the sandwich instances.
     * Overloaded, to cover both possible value-type-cases (String, List<String>)
     *
     * @param sandwich  (target of integration)
     * @param name  (the name-part of the NAME/VALUE pair)
     * @param value  (the value-part of the NAME/VALUE pair)
     */
    private static void integrate(Sandwich sandwich, String name, String value ) {
        switch (name) {
            case "mainName": { sandwich.setMainName(value); break; }
            case "placeOfOrigin": { sandwich.setPlaceOfOrigin(value); break; }
            case "description": { sandwich.setDescription(value); break; }
            case "image": { sandwich.setImage(value); break; }
            default: { Log.d("JsonUtil->integrate", "ERROR:  " + name ); break; }
        }
    }
    private static void integrate(Sandwich sandwich, String name, List<String> value ) {
        switch (name) {
            case "alsoKnownAs": { sandwich.setAlsoKnownAs(value); break; }
            case "ingredients": { sandwich.setIngredients(value); break; }
            default: { Log.d("JsonUtil->integrate", "ERROR:  " + name ); break; }
        }
    }
}