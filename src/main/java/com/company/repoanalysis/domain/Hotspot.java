package com.company.repoanalysis.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Hotspot {
    private String module;
    private int revisions;
    private int loc;

    public Hotspot(String module, int revisions, int loc){
        this.module = module;
        this.revisions = revisions;
        this.loc = loc;
    }

}

