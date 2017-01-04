package com.github.mvonrenteln.dsa.mailparser

import groovy.json.JsonSlurper

final String BR = "\r\n"
final String LEERZEILE = BR + BR

String input = args[0]
File outputFile = new File(new File(input).parentFile, "Abenteuer.adoc")
def excludes = ["Liskoms Turm (Nur AP)", "Die magischen Bücher"]

def jsonSlurper = new JsonSlurper()
def abenteuerListe = jsonSlurper.parse(new File(input))

outputFile.delete()
outputFile << "= Erlebnisse der 7 Gezeichneten$BR:toc:$LEERZEILE"

String kampagne
abenteuerListe.each { abenteuer ->
    if (!excludes.contains(abenteuer.abenteuer)) {
        if (!abenteuer.kampagne)
            abenteuer.kampagne = "Zwischen den Abenteuern"

        if (kampagne?.trim() != abenteuer.kampagne.trim()) {
            kampagne = abenteuer.kampagne
            outputFile << "== " + kampagne + LEERZEILE
        }
        outputFile << "=== " + abenteuer.abenteuer + LEERZEILE
        if (abenteuer.zitat) {
            outputFile << abenteuer.zitat + LEERZEILE
        }
        outputFile << formatiereDatumAlsÜberschrift(abenteuer.text) + LEERZEILE
    }
}

String formatiereDatumAlsÜberschrift(String text) {
    String aventurischesDatum = /(?:(?:\d{1,2}\.)?(?:(?: \w{3})? (?:und|bis) \d{1,2}\.)? )?\w{3} \d{4} BF/
    return text.replaceAll(~/([\r\n])($aventurischesDatum)/, { alles, whitespace, datum -> "$whitespace==== $datum" })
               .replaceAll(~/^$aventurischesDatum/,{ alles -> "==== $alles" })

}