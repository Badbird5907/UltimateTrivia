package dev.badbird.trivia.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;

public class CSVParser {
    public record Row(Map<String, String[]> cols) {
        public String getStrValue(String key) {
            return cols.get(key)[0];
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            cols.forEach((a,b) -> {
                sb.append(a).append(": ").append(Arrays.toString(b)).append(" | ");
            });
            return sb.toString();
        }
    }
    private List<Row> rows = new ArrayList<>();

    public CSVParser(File csv) {
        try {
            String lines = new String(Files.readAllBytes(csv.toPath()))
                    .replace("\r\n", "\n"); // got bit by carriage return last competition too wtf please stop
            String[] split = lines.split("\n");
            String[] cols = split[0].split(",");
            for (int i = 1; i < split.length; i++) { // loop thru each line
                String line = split[i];
                // simple fix to a edge case where two double quotes breaks parsing
                line = line.replace(",\"\"", ",\"%_DQ_%")
                        .replace("\"\",", "%_DQ_%\",");
                boolean inStr = false;
                StringBuilder current = new StringBuilder();
                Map<String, String[]> map = new HashMap<>();
                int colCount = 0;
                boolean last = false;
                List<String> lastList = new ArrayList<>();
                PrimitiveIterator.OfInt iterator = line.chars().iterator();

                Function<StringBuilder, String> strFunction = (c) -> { // this function cleans the string up
                    String s = Utils.unescapeHtml(c.toString().trim()).replace("%_DQ_%", "\"");
                    if (s.startsWith("\"") && s.endsWith("\"")) {
                        s = s.substring(1, s.length() - 1);
                    }
                    return s;
                };

                while (iterator.hasNext()) { // go through each char
                    int it = iterator.next();
                    char c = Character.toChars(it)[0]; // wtf
                    if (c == '"') { // parse strings
                        inStr = !inStr;
                    }
                    if (c == ',' && !inStr) { // check if it is a delim and we're not in a string
                        String s = strFunction.apply(current); // clean the string
                        if (!last) { // not the last one (list)
                            String colName = cols[colCount++];
                            // System.out.println("Found comma delim for col: " + colName + " | " + s);
                            map.put(colName.trim(), new String[]{s});
                        } else {
                            // System.out.println("Found comma delim at end, adding to list");
                            lastList.add(s);
                            map.put(cols[colCount].trim(), lastList.toArray(new String[0]));
                        }
                        current = new StringBuilder();
                        if ((colCount + 1) == cols.length) { // assume last col is a list
                            last = true;
                        }
                        continue;
                    }
                    current.append(c);
                }
                // flush out final one
                lastList.add(strFunction.apply(current));
                map.put(cols[colCount].trim(), lastList.toArray(new String[0]));
                rows.add(new Row(map));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Row> getRows() {
        return rows;
    }
}
