/*
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    Dieses Programm ist Freie Software: Sie können es unter den Bedingungen
    der GNU General Public License, wie von der Free Software Foundation,
    Version 3 der Lizenz oder (nach Ihrer Wahl) jeder neueren
    veröffentlichten Version, weiterverbreiten und/oder modifizieren.

    Dieses Programm wird in der Hoffnung, dass es nützlich sein wird, aber
    OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
    Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
    Siehe die GNU General Public License für weitere Details.

    Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
    Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package com.github.mvonrenteln.dsa.mailparser

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.lang3.text.WordUtils

import java.nio.file.Paths

String input = args[0]


def jsonSlurper = new JsonSlurper()
def abenteuerListe = jsonSlurper.parse(new File(input))

abenteuerListe.each { abenteuer ->
    abenteuer.abenteuer = abenteuer.abenteuer.trim()
    if (abenteuer.kampagne) {
        abenteuer.kampagne = abenteuer.kampagne.trim()
    }
    abenteuer.text = entferneUnerwünschtenText(entferneMehrfacheUmbrüche(abenteuer.text)).trim()

    int textEnde = abenteuer.text.indexOf("AP")
    if (textEnde != -1) {
        abenteuer.daten = abenteuer.text[textEnde.. -1].trim()
        abenteuer.text = abenteuer.text[0.. textEnde-1].trim()
    }

    println abenteuer.abenteuer
    println abenteuer.text
    println "*" * 200
}

Paths.get(input[0.. -6] + "_2.json").withWriter { jsonWriter ->
    jsonWriter.write JsonOutput.prettyPrint(JsonOutput.toJson(abenteuerListe))
}

String entferneMehrfacheUmbrüche(String text) {
    return text.replaceAll(~/(\r\n ?){3,}/, "\r\n\r\n")
}

String entferneUnerwünschtenText(String text) {
    def unerwünschteTexte = ["Viele Grüße.*"]
    unerwünschteTexte.each { text = text.replaceAll(it, "")}
    return text
}