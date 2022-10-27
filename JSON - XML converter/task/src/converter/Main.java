package converter;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var str = new Scanner(System.in)
                            .nextLine()
                            .trim();
        if (str.startsWith("<")) {
            var result = str
                              .replaceAll("[<>/]", " ")
                              .trim()
                              .split(" ");
            System.out.println("{" +
                    "\"" +
                    result[0] +
                    "\": " +
                    (result.length == 1
                            ?
                            null + "}"
                            :
                            "\"" + result[1] + "\"}"));

        } else if (str.startsWith("{")) {
            var result = str
                              .replaceAll("[{}\" ]", "")
                              .trim()
                              .split(":");

            if (!"null".equals(result[1])) {
                System.out.printf(
                        "<%s>%s</%s>",
                        result[0],
                        result[1],
                        result[0]);
            } else {
                System.out.printf("<%s/>", result[0]);
            }
        } else {
            System.out.println("Unknown data format");
        }
    }
}

