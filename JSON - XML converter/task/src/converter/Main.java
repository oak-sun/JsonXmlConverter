package converter;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        var path = "test.txt";
        var str = getData(path);
        if (Objects.requireNonNull(str).startsWith("<")) {
            xmlToJson(str);
        } else {
            jsonToXml(str);
        }
    }

    private static String getData(String path) {
        try (var bReader = new BufferedReader(
                           new FileReader(path)))
        {
            var sb = new StringBuilder();
            while (bReader.ready()) {
                sb.append(bReader.readLine());
            }
            return sb.toString();

        } catch (Exception e) {
            System.out.println("Something went wrong!");
            return null;
        }
    }

    private static void xmlToJson(String str) {
        String tag;
        String value;
        List<String> attributes = new ArrayList<>();
        var tagM = Pattern
                                .compile("<[\\w/.]+")
                                .matcher(str);
        var valueM = Pattern
                                     .compile(">[\\w\\s.]+<")
                                    .matcher(str);
        var attributeM = Pattern
                                       .compile("\\w+ = \"\\w+\"")
                                      .matcher(str);

        tag = tagM.find() ?
                tagM
                        .group()
                        .replaceAll("[</>]", "")
                : "";
        value = valueM.find() ?
                valueM
                        .group()
                        .replaceAll("[<>]", "\"")
                : null;
        while (attributeM.find()) {
            attributes.add(attributeM.group());
        }

        if (attributes.isEmpty()) {
            if (Objects.requireNonNull(value).isEmpty()) {
                System.out.printf(
                        "{\"%s\" : %s}%n", tag, null);
            } else {
                System.out.printf(
                        "{\"%s\" : \"%s\"}%n", tag, value);
            }

        } else {
            var attr = attributes
                    .stream()
                    .map(x ->
                            "\t\t\"@" +
                            x.replaceAll(" =", "\" :") +
                            ",\n")
                    .reduce(String::concat).get();
            var result = String.format(
                    "{\n \t\"%s\" : \n\t{\n %s \t\t\"#%s\" : %s\n\t}\n}",
                    tag,
                    attr,
                    tag,
                    value);
            System.out.println(result);
        }
    }
    private static void jsonToXml(String str) {
        String tag;
        String value;
        List<String> attributes = new ArrayList<>();
        var tagJ = Pattern
                                 .compile("\"[\\w.]+\"")
                                 .matcher(str);
        var valueJ = Pattern
                                 .compile("\"#[\\w.]+\" : (?!null)[\"\\w\\s]+")
                                 .matcher(str);
        var attrJ = Pattern
                                       .compile("\"@[\\w.]+\" : [\"\\w]+")
                                       .matcher(str);
        while (attrJ.find()) {
            attributes.add(attrJ.group());
        }
        tag = tagJ.find() ?
                tagJ
                        .group()
                        .replaceAll("\"", "")
                : "";
        value = valueJ.find() ?
                valueJ
                        .group()
                        .replaceAll("\"#[\\w.]+\" : ", "")
                : "";
        var attr = attributes
                .stream()
                .map(x -> (x.matches(".+\\d+") ?
                        (x + "\"")
                        : x))
                .map(x -> x.replaceAll("\"@", " "))
                .map(x -> x.replaceAll("\" : \"?", " = \""))
                .reduce(String::concat)
                .orElse("");

        if (value.isEmpty()) {
            System.out.printf("<%s%s/>%n", tag, attr);

        } else {
            value = value.replaceAll("\"", "");
            System.out.printf("<%s%s>%s</%s>%n",
                    tag,
                    attr,
                    value,
                    tag);
        }
    }
}

