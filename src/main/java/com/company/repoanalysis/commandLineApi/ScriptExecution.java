package com.company.repoanalysis.commandLineApi;

import com.company.repoanalysis.exceptions.InvalidOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.Date;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Component
public class ScriptExecution {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ScriptConfig configuration;

    private String fileDirectory;

    private String reference;
    private String fileName;

    private Date before;
    private Date after;



    public void initializeParameters(String reference, String fileName, Date before, Date after){
        this.reference = reference;
        this.fileName = fileName;
        if(before!=null) this.before = before;
        if(after != null) this.after = after;
    }

    public void setFileDirectory(String fileDirectory) {
        String basePath = new File("").getAbsolutePath() + "\\src\\main\\resources\\git_Repos\\" + fileName
                + "\\";
        this.fileDirectory =basePath + fileDirectory;
    }

    private void setDefaultDate(){
        long millis=System.currentTimeMillis();
        this.before = new Date(millis);
    }



    public void executeScript(String operation) throws InterruptedException, IOException, InvalidOperationException {
        String basePath = new File("").getAbsolutePath() + "\\src\\main\\resources\\scripts\\";
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows) {
            if (operation.equals("getMetrics")) {
                builder.command(basePath + "metrics.bat");
            } else if (operation.equals("delete")) {
                builder.command(basePath + "deletion.bat");
            } else if (operation.equals("clone")) {
                builder.command(basePath + "clone.bat");
                builder.environment().put("REFERENCE", reference);
            } else if (operation.equals("list")) {
                builder.command(basePath + "list.bat");
            } else if (operation.equals("cloc")) {
                builder.command(basePath + "cloc.bat");
                builder.environment().put("FILE_NAME", fileName);
            } else if (operation.equals("merge")) {
                builder.command(basePath + "merge.bat");
            } else if (operation.equals("revisions")) {
                builder.command(basePath + "revisions.bat");
            } else if (operation.equals("log")) {
                builder.command(basePath + "log.bat");
                builder.environment().put("FILE_NAME", fileName);
                builder.environment().put("FLAG_BEFORE", "--before");
                builder.environment().put("DATE_BEFORE", before.toString());
                if (before != null) {
                    builder.environment().put("FLAG_BEFORE", "--before");
                    builder.environment().put("DATE_BEFORE", before.toString());
                }
                if (after != null) {
                    builder.environment().put("FLAG_AFTER", "--after");
                    builder.environment().put("DATE_AFTER", after.toString());
                }
                if (before == null && after == null) this.setDefaultDate();
            }else if(operation.equals("indentation")){
                builder.command(basePath + "indentation.bat");
                builder.environment().put("FILE_DIRECTORY", fileDirectory);
            }else {
            throw new InvalidOperationException("Invalid operation: " + operation);
        }
        } else {
            builder.command("sh", "-c", "ls");
        }
        builder.environment().put("PYTHON", configuration.getPython());
        builder.environment().put("CLOC", configuration.getCloc());
        builder.environment().put("PERL", configuration.getPerl());
        builder.directory(new File(System.getProperty("user.home")));
        Process process = builder.start();

        StreamGobbler streamGobbler =
                new StreamGobbler(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null){
                System.out.println(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int exitCode = process.waitFor();
        System.out.println(exitCode);
        assert exitCode == 0;
    }

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }
}
