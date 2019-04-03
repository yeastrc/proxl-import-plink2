package org.yeastrc.proxl.xml.plink2.objects;

import java.util.List;
import java.util.Objects;

public class PLinkLinkerEnd {

    public PLinkLinkerEnd(List<String> residues, boolean linksNTerminus, boolean linksCTerminus) {
        this.residues = residues;
        this.linksNTerminus = linksNTerminus;
        this.linksCTerminus = linksCTerminus;
    }

    @Override
    public String toString() {
        return "PLinkLinkerEnd{" +
                "residues=" + residues +
                ", linksNTerminus=" + linksNTerminus +
                ", linksCTerminus=" + linksCTerminus +
                '}';
    }

    public List<String> getResidues() {
        return residues;
    }

    public boolean isLinksNTerminus() {
        return linksNTerminus;
    }

    public boolean isLinksCTerminus() {
        return linksCTerminus;
    }

    private List<String> residues;
    private boolean linksNTerminus;
    private boolean linksCTerminus;
}
