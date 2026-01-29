package com.shinhan.spp.util.excel;

import java.util.*;

final class HeaderLayout {
    static final class CellSpec {
        final String label; final int startCol; final int colSpan; final int rowSpan;
        CellSpec(String label, int startCol, int colSpan, int rowSpan) {
            this.label = label; this.startCol = startCol; this.colSpan = colSpan; this.rowSpan = rowSpan;
        }
    }

    final int maxDepth;
    final List<List<CellSpec>> rows;

    private HeaderLayout(int maxDepth, List<List<CellSpec>> rows) {
        this.maxDepth = maxDepth;
        this.rows = rows;
    }

    static HeaderLayout from(List<ColumnMeta> cols) {
        int n = cols.size();
        int maxDepth = 1;
        for (ColumnMeta c : cols) if (c.headerPath.size() > maxDepth) maxDepth = c.headerPath.size();

        List<List<String>> labels = new ArrayList<>();
        for (int l = 0; l < maxDepth; l++) {
            labels.add(new ArrayList<>(Collections.nCopies(n, null)));
        }
        for (int c = 0; c < n; c++) {
            List<String> path = cols.get(c).headerPath;
            for (int l = 0; l < path.size(); l++) labels.get(l).set(c, path.get(l));
        }

        List<List<CellSpec>> out = new ArrayList<>();
        for (int l = 0; l < maxDepth; l++) {
            List<CellSpec> levelCells = new ArrayList<>();
            int c = 0;
            while (c < n) {
                String lab = labels.get(l).get(c);
                boolean terminal = (cols.get(c).headerPath.size() == l + 1);
                if (lab == null) { c++; continue; }

                int run = 1;
                while (c + run < n) {
                    String lab2 = labels.get(l).get(c + run);
                    boolean term2 = (cols.get(c + run).headerPath.size() == l + 1);
                    if (!Objects.equals(lab2, lab) || term2 != terminal) break;
                    run++;
                }

                int rowSpan = terminal ? (maxDepth - l) : 1;
                levelCells.add(new CellSpec(lab, c, run, rowSpan));
                c += run;
            }
            out.add(levelCells);
        }
        return new HeaderLayout(maxDepth, out);
    }
}
