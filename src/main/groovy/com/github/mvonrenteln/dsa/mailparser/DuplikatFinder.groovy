package com.github.mvonrenteln.dsa.mailparser

import groovy.json.JsonSlurper
import org.apache.commons.lang3.StringUtils

String input = args[0]


def jsonSlurper = new JsonSlurper()
def abenteuerListe = jsonSlurper.parse(new File(input))

def bereitsGeseheneAbenteuer = [:]

abenteuerListe.each { abenteuer ->
    if (bereitsGeseheneAbenteuer.containsKey(abenteuer.abenteuer)) {
        println abenteuer.abenteuer
        println bereitsGeseheneAbenteuer.get(abenteuer.abenteuer)
        println "*" * 200
        println abenteuer.text
        println "*" * 200
    } else {
        bereitsGeseheneAbenteuer.put(abenteuer.abenteuer, abenteuer.text)
    }
}