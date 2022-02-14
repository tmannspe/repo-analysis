package com.company.repoanalysis.commandLineApi;

import com.company.repoanalysis.domain.Hotspot;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
public class MetricReader {

    private String basePath;

    public MetricReader(){
        this.initializeBasePath();
    }

    private void initializeBasePath(){
        this.basePath = new File("").getAbsolutePath() + "\\src\\main\\resources\\logs\\";
    }

    public HashMap<String, Integer> getMetrics() throws IOException {
        HashMap<String, Integer> metrics = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(basePath + "metrics.csv"));
            boolean completed = false;
            while (!completed){
                String line = reader.readLine();
                if (line.contains("number-of")){
                    Integer number = Integer.parseInt(line.substring(line.indexOf(",")+1));
                    metrics.put(line.substring(0, line.indexOf(",")), number);
                    if (metrics.containsKey("number-of-authors"))
                        completed = true;
                }

            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return metrics;
    }

    public boolean checkRepos(String name) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(basePath + "list.txt"));
            String line;
            while ((line = reader.readLine()) != null){
                if(line.contains(name))
                    return true;
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public HashSet<Hotspot> getHotspots() throws IOException {
        HashSet<Hotspot> hotspots = new HashSet<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(basePath + "merge.csv"));
            String line;
            while ((line = reader.readLine()) != null){
                if(line.contains("module")) continue;
                String module = line.substring(0, line.indexOf(","));
                Integer revisions = Integer.parseInt(line.substring(line.indexOf(",")+1, line.lastIndexOf(",")));
                Integer loc = Integer.parseInt(line.substring(line.lastIndexOf(",")+1, line.length()));
                hotspots.add(new Hotspot(module, revisions.intValue(), loc.intValue()));
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return hotspots;
    }

    public boolean getIndentation() throws IOException {
        int complexity_counter = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(basePath + "indentation.csv"));
            reader.readLine();
            String line = reader.readLine();
            String substring = line.substring(line.indexOf("," , line.indexOf(",") + 1) + 1) ;
            Float mean = Float.parseFloat(substring.substring(0, substring.indexOf(",")));
            Float sd = Float.parseFloat(substring.substring(substring.indexOf(",") + 1, substring.lastIndexOf(",")));
            Float max = Float.parseFloat(substring.substring(substring.lastIndexOf(",") + 1));
            if (mean.floatValue() >= 2) complexity_counter += 2;
            if (sd.floatValue() >= 2) complexity_counter++;
            if (max.floatValue() >= 10) complexity_counter++;
            if (complexity_counter >= 2) return true;
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
