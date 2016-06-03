package com.cameronleger.neuralstyle;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class NeuralStyle {
    public static String executable = "th";
    public static File neuralStylePath = new File("/home/cameron/neural-style");
    public Image styleImage;
    public Image contentImage;

    public NeuralStyle() {

    }

    public boolean checkArguments() {
        if (neuralStylePath == null || !neuralStylePath.isDirectory())
            return false;
        if (styleImage == null || styleImage.getPath() == null || !styleImage.getPath().isFile())
            return false;
        if (contentImage == null || contentImage.getPath() == null || !contentImage.getPath().isFile())
            return false;
        return true;
    }

    private String[] buildCommand() {
        return new String[] {
                executable,
                "neural_style.lua",
                "-style_image",
                styleImage.getPath().getAbsolutePath(),
                "-content_image",
                contentImage.getPath().getAbsolutePath()
        };
    }

    public void start() {
        if (!checkArguments())
            return;

        for (String s : buildCommand()) {
            System.out.print(s);
            System.out.print(" ");
        }

        try {
            String line;
            Process p = Runtime.getRuntime().exec(buildCommand(), null, neuralStylePath);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            System.out.println("Input:");
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            input.close();

            System.out.println("Error:");
            while ((line = error.readLine()) != null) {
                System.out.println(line);
            }
            error.close();

            System.out.println("Exit: " + String.valueOf(p.waitFor()));
            System.out.println("Done.");
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }
}
