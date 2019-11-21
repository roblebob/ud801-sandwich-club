package com.udacity.sandwichclub.utils;

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
     * Removes all whitespaces "\s", which includes tabs "\t", newlines "\n", .... from the json string.
     * It also removes the all-enclosing-curly-brackets, signature of the main json object's specific syntax
     *
     * @param json : String
     * @return  : String
     */
    private static String cleanup(String json) {
        return json
                .replaceAll("\\s*\\{\\s*", "")
                .replaceAll("\\s*\\}\\s*", "")
                .replaceAll("\"\\s*:\\s*\"", "\":\"")
                .replaceAll("\"\\s*:\\s*\\[\\s*", "\":\\[")
                .replaceAll("\"\\s*,\\s*\"", "\",\"")
                .replaceAll("\\s*\\]\\s*,\\s*\"", "\\],\"")
                .replaceAll("\\s*\\]\\s*", "\\]");
    }

    /**
     * Performing splitting at every comma ","  gives rise to 3 x distinct cases (1 x true, 2 x false/positives).
     * (1:true)  INTER (splits) -- separates individual NAME/VALUE-pairs from each other.
     * (2:false)  INTRA (splits) <<<< commas used in an array of Strings constituting one possible VALUE instance
     * (3:false)  ATOMIC (splits) <<<< commas within a single String constituting the other possible VALUE instance
     *
     *  Note: The strings defining the sandwich fieldnames are known and do not contain any commas (see Sandwich.java)
     *      Therefore there are no ATOMIC splits within the NAME definition part.
     *
     * @param json : String
     * @return list : List<String>
     */
    private static List<String> intoListOfAttributesStrings(String json) {

        List<String> list =  new LinkedList<>(Arrays.asList(json.split(",")));

        ListIterator<String> iter = list.listIterator();
        while (iter.hasNext()) {

            /* Getting into position */
            String _last = iter.next();
            if (!iter.hasNext()) break;
            String _curr = iter.next();
            iter.previous();

            boolean cond = _curr.contains("\":\"") || _curr.contains("\":[\"");
            if (!cond) /* Equivalent case identifying the false/positives */ {

                iter.remove();
                iter.previous();
                iter.set(_last + "," + _curr);
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

            String _curr = iter.next().toString();
            iter.remove();

            if (_curr.matches("\"\\w*\":\".*\"")) /* case: value type is a single string */{

                String[] _pair = _curr  .substring(1, _curr.length() - 1)   .split("\":\"", 2);
                integrate(sandwich, _pair[0], _pair[1]);

            } else if (_curr.matches("\"\\w*\":\\[\".*\"\\]")) /* case: value type is a string array */{

                String[] _pair = _curr  .substring(1, _curr.length() - 2)   .split("\":\\[\"" , 2);
                integrate(sandwich, _pair[0], new LinkedList<String>(Arrays.asList(_pair[1].split("\",\""))));

            } else System.out.println("Error@integrateInto:  " + _curr );
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
            default: System.out.println("Error@integrate(1):  " + name + "  cannot be identified"); break;
        }
    }
    private static void integrate(Sandwich sandwich, String name, List<String> value ) {
        switch (name) {
            case "alsoKnownAs": { sandwich.setAlsoKnownAs(value); break; }
            case "ingredients": { sandwich.setIngredients(value); break; }
            default: System.out.println("Error@integrate(2):  " + name + "  cannot be identified"); break;
        }
    }

    /**
     *
     * @return
     */
    public static boolean runTest() {

        final String TEST_STRING =  "    {" + "\n" +
                "\"mainName\" : "       + "\"Manhattan0816\""                                           + " , " + "\n" + "\t" +
                "\"alsoKnownAs\" : "    + "[ \"Manhatten0815+\", \"N.Y.Buster\", \"ManhattatorXtra\"  ]" + " , " + "\n" +
                "\"placeOfOrigin\" : "  + "\"Dresden, Saxony, Germany\""                                         + " , " + "\n" +
                "\"description\" : "    + "\"Meatballs made from plant, with BBQ-saurce, Cheddar, ... .\""  + " , " + "\n" +
                "\"image\" : "          + "\"\""                                                        + " , " + "\n" +
                "\"ingredients\" : "    + "[    \"Baguette, 50cm\" ,   \"sliced VeggieBurgers\" , \"Onions\"  ]" + "\n" + "}           ";

        final Sandwich TEST_SANDWICH =  new Sandwich("Manhattan0816",
                new LinkedList<String>(Arrays.asList("Manhatten0815+", "N.Y.Buster", "ManhattatorXtra")),
                "Dresden, Saxony, Germany",
                "Meatballs made from plant, with BBQ-saurce, Cheddar, ... .",
                "",
                new LinkedList<String>(Arrays.asList("Baguette, 50cm", "sliced VeggieBurgers", "Onions"))
        );

        final Sandwich sandwich =  JsonUtils.parseSandwichJson(TEST_STRING);

        return sandwich.getMainName().equals(TEST_SANDWICH.getMainName()) &&
                sandwich.getAlsoKnownAs().equals(TEST_SANDWICH.getAlsoKnownAs()) &&
                sandwich.getPlaceOfOrigin().equals(TEST_SANDWICH.getPlaceOfOrigin()) &&
                sandwich.getDescription().equals(TEST_SANDWICH.getDescription()) &&
                sandwich.getImage().equals(TEST_SANDWICH.getImage()) &&
                sandwich.getIngredients().equals(TEST_SANDWICH.getIngredients());
    }
}

