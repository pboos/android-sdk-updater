package ch.pboos.android.sdkupdater;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateRunner {

    public static void main(String... args) {
        if (args.length != 1 || StringUtils.isEmpty(args[0])) {
            System.out.println("To run use:");
            System.out.println("export ANDROID_HOME=/path/to/android-sdk");
            System.out.println("./run.sh");
            return;
        }
        String androidHome = args[0];
        Pattern[] installPatterns = new Pattern[]{
                Pattern.compile("^tools$"),
                Pattern.compile("^platform-tools$"),
                Pattern.compile("^build-tools-.*"),
                Pattern.compile("^android-.*"),
                Pattern.compile(".*m2repository.*"),
        };


        System.out.println("########## START ##########");
        runUpdate(androidHome, installPatterns);
        System.out.println("########## END ##########");
    }

    private static void runUpdate(String androidHome, Pattern[] installPatterns) {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(androidHome + "/tools/android list sdk -a -u -e");
            String output = IOUtils.toString(process.getInputStream());

            Pattern pattern = Pattern.compile("-{5,}\\nid: [0-9]+ or \"([0-9a-zA-Z\\.\\-_]+)\"");
            Matcher matcher = pattern.matcher(output);
            ArrayList<String> installItems = new ArrayList<String>();
            while (matcher.find()) {
                String installableId = matcher.group(1);
                for (Pattern installPattern : installPatterns) {
                    if (installPattern.matcher(installableId).find()) {
                        installItems.add(installableId);
                    }
                }
            }

            ///////////////////////////////////////////////
            ///////////////////////////////////////////////

            String command = androidHome + "/tools/android update sdk -u -a --filter " + StringUtils
                    .join(installItems, ",");
            System.out.println(command);

            process = runtime.exec(command);

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            process.getOutputStream().write("y\r".getBytes());
            process.getOutputStream().flush();

            Pattern patternInstalling = Pattern.compile(".*Installing.*");
            while ((line = in.readLine()) != null) {
                if (patternInstalling.matcher(line).find()) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Something went wrong", e);
        }
    }
}
