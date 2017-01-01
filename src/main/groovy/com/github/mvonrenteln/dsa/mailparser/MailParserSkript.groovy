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

import com.auxilii.msgparser.Message
import com.auxilii.msgparser.MsgParser
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter

MsgParser msgParser = new MsgParser();

def mails = new TreeSet<Mail>({ Mail o1, Mail o2 -> o1.datum.compareTo(o2.datum) })

def mailVerzeichnis = args[0]
println "Lese MSG-Dateien in ${ mailVerzeichnis}"
new File(mailVerzeichnis).eachFile() { file ->
    try {
        if(file.name.endsWith('.msg')) {
            Message msg = msgParser.parseMsg(file.absolutePath);
            mails << (new Mail(subject:msg.subject,
                       datum:msg.date,
                       kampagne:getKampagne(msg.subject),
                       abenteuer:getAbenteuer(msg.subject),
                       text:msg.bodyText))
        }
    } catch (Exception e) {
        println "$file.name fehlerhaft!"
        println e
    }

}

def format = CSVFormat.DEFAULT.withHeader("Datum", "Mail-Titel", "Kampagne", "Abenteuer", "Text")
def csvFilePrinter = new CSVPrinter(new FileWriter(mailVerzeichnis + "/DSA-Mails.csv"), format);
mails.each { Mail mail ->
    String datum = mail.datum.format("dd.MM.yyyy hh:mm")
    println mail.kampagne.padRight(30) + " - " + mail.abenteuer.padRight(50) + datum
    csvFilePrinter.printRecord([datum, mail.subject, mail.kampagne, mail.abenteuer, mail.text])
}

String getKampagne(String subject) {
    subject = entfernePrefix(subject)
    int ende = getTrennerPos(subject)
    if (ende == -1) {
        println "In $subject konnte die Kampagne nicht ermittelt werden!"
        return ""
    }
    return subject.substring(0, ende)
}

String getAbenteuer(String subject) {
    subject = entfernePrefix(subject)
    int anfang = getTrennerPos(subject)
    if (anfang == -1) {
        println "In $subject konnte das Abenteuer nicht ermittelt werden!"
        return subject
    }
    return subject.substring(anfang+1, subject.length()).replace("\"", "").trim()
}

private int getTrennerPos(String subject) {
    subject.indexOf(":") != -1 ? subject.indexOf(":") : subject.indexOf("-")
}

String entfernePrefix(String subject) {
    def mailingListenPrefixe = ~/\[.+?\]/
    return subject.replaceAll(mailingListenPrefixe, "").trim()
}

class Mail {
    String subject, kampagne, abenteuer, text
    Date datum
}