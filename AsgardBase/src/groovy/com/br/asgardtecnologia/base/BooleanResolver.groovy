package com.br.asgardtecnologia.base

class BooleanResolver {
    static solveRuleMultiple(rules, value) {
        def ret = []
        for (rule in rules) {
            // Boolean solution or Value equals
            if (((rule[0]?.getClass() == java.lang.Boolean) && rule[0]) || (rule[0]?.toString() == value?.toString())) {
                ret << rule[1]
                continue
            }
        }

        return ret
    }
}
