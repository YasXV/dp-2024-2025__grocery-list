package com.fges;

import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles command line options parsing
 */
public class MyOptions {
    private final Options options;
    private CommandLine cmd;

    private String sourceFile = "groceries";
    private String format = "json";
    private String category = "default";
    private String command;
    private List<String> commandArgs = new ArrayList<>();

    public MyOptions() {
        options = new Options();

        // Define options
        options.addOption(Option.builder("f")
                .longOpt("file")
                .hasArg()
                .desc("Source file name (default: groceries)")
                .build());

        options.addOption(Option.builder("t")
                .longOpt("type")
                .hasArg()
                .desc("File format: json or csv (default: json)")
                .build());

        options.addOption(Option.builder("c")
                .longOpt("category")
                .hasArg()
                .desc("Item category (default: default)")
                .build());

        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Display help")
                .build());
    }

    /**
     * Parse command line arguments
     * @param args Command line arguments
     * @return True if parsing was successful, false otherwise
     */
    public boolean parse(String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            cmd = parser.parse(options, args, true);

            // Check if help is requested
            if (cmd.hasOption("h")) {
                formatter.printHelp("grocery-manager", options);
                return false;
            }

            // Extract options
            if (cmd.hasOption("f")) {
                sourceFile = cmd.getOptionValue("f");
            }

            if (cmd.hasOption("t")) {
                format = cmd.getOptionValue("t").toLowerCase();
                if (!format.equals("json") && !format.equals("csv")) {
                    System.err.println("Unsupported format: " + format);
                    return false;
                }
            }

            if (cmd.hasOption("c")) {
                category = cmd.getOptionValue("c");
            }

            // Extract command and its arguments
            List<String> argList = Arrays.asList(cmd.getArgs());
            if (argList.isEmpty()) {
                System.err.println("No command specified");
                formatter.printHelp("grocery-manager", options);
                return false;
            }

            command = argList.get(0);
            if (argList.size() > 1) {
                commandArgs = argList.subList(1, argList.size());
            }

            return true;
        } catch (ParseException e) {
            System.err.println("Error parsing arguments: " + e.getMessage());
            formatter.printHelp("grocery-manager", options);
            return false;
        }
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public String getFormat() {
        return format;
    }

    public String getCategory() {
        return category;
    }

    public String getCommand() {
        return command;
    }

    public List<String> getCommandArgs() {
        return commandArgs;
    }
}