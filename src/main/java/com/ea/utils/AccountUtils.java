package com.ea.utils;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class AccountUtils {

    /**
     * Generates alternate names
     * @param alts Number of alternate names to provide if duplicate name is found
     * @param name Duplicated name
     * @return
     */
    public static String suggestNames(int alts, String name) {
        Set<String> opts = new LinkedHashSet<>();

        if(name.length() > 8) {
            name = name.substring(0, 7);
        }

        for(int i = 1; i <= alts; i++) {
            if(i == 1) {
                opts.add(name + "Kid");
            } else if(i == 2) {
                opts.add(name + "Rule");
            } else {
                opts.add(name + ThreadLocalRandom.current().nextInt(1000, 10000));
            }
        }
        return opts.stream().map(s -> s.substring(0, s.length() > 12 ? 11 : s.length())).collect(Collectors.joining(","));
    }

}
