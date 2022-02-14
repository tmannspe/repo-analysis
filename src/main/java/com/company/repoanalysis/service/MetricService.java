package com.company.repoanalysis.service;

import com.company.repoanalysis.commandLineApi.MetricReader;
import com.company.repoanalysis.commandLineApi.ScriptExecution;
import com.company.repoanalysis.domain.Hotspot;
import com.company.repoanalysis.exceptions.InvalidOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.util.*;

@Service
public class MetricService {
    @Autowired
    private ScriptExecution scriptExecution;

    @Autowired
    private MetricReader metricReader;

    public HashMap<String, Integer> getMetrics(Date before, Date after, String reference) throws IOException, InterruptedException, InvalidOperationException {
        String file_name = this.getFileNameFromReference(reference);
        scriptExecution.initializeParameters(reference, file_name, before, after);
        scriptExecution.executeScript("list");
        if (!metricReader.checkRepos(file_name)) scriptExecution.executeScript("clone");
        scriptExecution.executeScript("log");
        scriptExecution.executeScript("getMetrics");
        HashMap<String, Integer> metricList = metricReader.getMetrics();
        return metricList;
    }

    public HashSet<Hotspot> getHotspots(Date before, Date after, String reference) throws InterruptedException, IOException, InvalidOperationException {
        String file_name = this.getFileNameFromReference(reference);
        scriptExecution.initializeParameters(reference, file_name, before, after);
        scriptExecution.executeScript("list");
        if (!metricReader.checkRepos(file_name)) scriptExecution.executeScript("clone");
        scriptExecution.executeScript("log");
        scriptExecution.executeScript("cloc");
        scriptExecution.executeScript("revisions");
        scriptExecution.executeScript("merge");
        HashSet<Hotspot> hotspots = metricReader.getHotspots();
        this.filterHotspots(hotspots);
        scriptExecution.executeScript("delete");
        return hotspots;
    }


    private String getFileNameFromReference(String reference){
        int index_begin = reference.lastIndexOf("/")+1;
        int index_end = reference.indexOf(".git");
        return reference.substring(index_begin, index_end);
    }

    private HashSet<Hotspot> filterHotspots(HashSet<Hotspot> hotspots)
            throws InterruptedException, IOException, InvalidOperationException {
        HashSet<Hotspot> filteredHotspots = this.filterByName(hotspots);
        filteredHotspots = this.filterByComplexityAndRevisions(filteredHotspots);
        Iterator<Hotspot> hotspotIterator = hotspots.iterator();
        while (hotspotIterator.hasNext()){
            if (!this.filterByIndentation(hotspotIterator.next().getModule()))
                hotspotIterator.remove();
        }
        return filteredHotspots;
    }

    private HashSet<Hotspot> filterByName(HashSet<Hotspot> hotspots){
        HashSet<String> falsePositives = new HashSet<>();
        falsePositives.add("build");
        falsePositives.add("gradle");
        falsePositives.add("pom");
        falsePositives.add("application.yml");
        falsePositives.add("application.properties");
        falsePositives.add(".html");
        falsePositives.add("Test");
        Iterator<Hotspot> hotspotIterator = hotspots.iterator();
        String hotspot;
        String falsePositive;
        int i  = 0;
        while (hotspotIterator.hasNext()) {
            hotspot = hotspotIterator.next().getModule();
            Iterator<String> falsePositiveIterator = falsePositives.iterator();
            while (falsePositiveIterator.hasNext()) {
                falsePositive = falsePositiveIterator.next();
                if (hotspot.contains(falsePositive)){
                    hotspotIterator.remove();
                }
            }
        }
        return hotspots;
    }

    private boolean filterByIndentation(String module) throws IOException, InvalidOperationException, InterruptedException {
        scriptExecution.setFileDirectory(module);
        scriptExecution.executeScript("indentation");
        return metricReader.getIndentation();
    }

    private HashSet<Hotspot> filterByComplexityAndRevisions (HashSet<Hotspot> hotspots) {
        int revisionSum = 0;
        int locSum = 0;
        for(Hotspot hotspot : hotspots){
            revisionSum += hotspot.getRevisions();
            locSum += hotspot.getLoc();
        }
        Float revisionMean = (float) revisionSum/hotspots.size();
        Float locMean = (float) locSum/hotspots.size();
        Iterator<Hotspot> hotspotIterator = hotspots.iterator();
        while (hotspotIterator.hasNext()) {
            Hotspot hotspot = hotspotIterator.next();
            if (!((float) hotspot.getRevisions() >= revisionMean &&
                    (float) hotspot.getLoc() >= locMean))
                hotspotIterator.remove();
        }
        return hotspots;
    }
}
