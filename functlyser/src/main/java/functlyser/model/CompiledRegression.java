package functlyser.model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class CompiledRegression {

    private int colNo;

    private Double meanM;

    private Double stdDevM;

    private Double meanC;

    private Double stdDevC;

    public int getColNo() {
        return colNo;
    }

    public void setColNo(int colNo) {
        this.colNo = colNo;
    }

    public Double getMeanM() {
        return meanM;
    }

    public void setMeanM(Double meanM) {
        this.meanM = meanM;
    }

    public Double getStdDevM() {
        return stdDevM;
    }

    public void setStdDevM(Double stdDevM) {
        this.stdDevM = stdDevM;
    }

    public Double getMeanC() {
        return meanC;
    }

    public void setMeanC(Double meanC) {
        this.meanC = meanC;
    }

    public Double getStdDevC() {
        return stdDevC;
    }

    public void setStdDevC(Double stdDevC) {
        this.stdDevC = stdDevC;
    }

    public static CompiledRegression compiledRegression(int colNo, Iterable<Regression> regressionIterator) {
        CompiledRegression compiledRegression = new CompiledRegression();
        compiledRegression.setColNo(colNo);

        List<Pair<Double, Double>> list = new ArrayList<>();
        Double mMean = 0.0;
        Double cMean = 0.0;
        for (Regression regression : regressionIterator) {
            Double m1 = regression.getM1();
            Double m2 = regression.getM2();
            Double m = (m2 == 0.0 ? 0.0 : m1 / m2);

            Double c1 = regression.getC1();
            Double c2 = regression.getC2();
            Double c = (c2 == 0.0 ? 0.0 : c1 / c2);

            list.add(new Pair<>(m, c));
            mMean += m;
            cMean += c;
        }
        if (list.size() == 0) {
            return compiledRegression;
        }
        mMean = mMean / list.size();
        cMean = cMean / list.size();

        Double mStdDev = 0.0;
        Double cStdDev = 0.0;
        for (Pair<Double, Double> elem : list) {
            mStdDev += Math.pow(elem.getKey() - mMean, 2);
            cStdDev += Math.pow(elem.getValue() - cMean, 2);
        }
        mStdDev = Math.sqrt(mStdDev / list.size());
        cStdDev = Math.sqrt(cStdDev / list.size());

        compiledRegression.setMeanM(mMean);
        compiledRegression.setStdDevM(mStdDev);
        compiledRegression.setMeanC(cMean);
        compiledRegression.setStdDevC(cStdDev);
        return compiledRegression;
    }
}
