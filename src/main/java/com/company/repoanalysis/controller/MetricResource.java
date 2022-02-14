package com.company.repoanalysis.controller;

import com.company.repoanalysis.domain.Hotspot;
import com.company.repoanalysis.service.MetricService;
import com.company.repoanalysis.exceptions.InvalidOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@RestController
public class MetricResource {
    @Autowired
    MetricService service;

    @GetMapping("/metrics")
    public HashMap<String, Integer> getMetrics(@RequestParam(required = false) String before,
                                               @RequestParam(required = false) String after,
                                               @RequestBody String reference) throws IOException, InterruptedException, InvalidOperationException {
        if (before != null && after !=null) {
            return service.getMetrics(Date.valueOf(before), Date.valueOf(after), reference);
        }else if (before != null){
            return service.getMetrics(Date.valueOf(before), null, reference);
        }else if (after != null){
            return service.getMetrics(null, Date.valueOf(after), reference);
        }else{
            return service.getMetrics(null, null, reference);
        }
    }
    @GetMapping("/metrics/hotspots")
    public HashSet<Hotspot> getHotspots(@RequestParam(required = false) String before,
                                        @RequestParam(required = false) String after,
                                        @RequestBody String reference) throws IOException, InterruptedException, InvalidOperationException {
        if (before != null && after !=null) {
            return service.getHotspots(Date.valueOf(before), Date.valueOf(after), reference);
        }else if (before != null){
            return service.getHotspots(Date.valueOf(before), null, reference);
        }else if (after != null){
            return service.getHotspots(null, Date.valueOf(after), reference);
        }else{
            return service.getHotspots(null, null, reference);
        }
    }
}
