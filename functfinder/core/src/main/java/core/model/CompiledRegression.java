package core.model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class CompiledRegression {

    private int colNo;

    private Double meanM;

    private Double stdDevM;

    private Double meanC;

    private Double stdDevC;

    private Double weightedMeanM;

    private Double weightedStdDevM;

    private Double weightedMeanC;

    private Double weightedStdDevC;

    private Double meanR;

    private Double stdDevR;

    private Long numberOfOutliers;

    private Long numberOfClusters;

    private Double avgNumberOfPointsInCluster;

    private Double stdDevAvgNumberOfPointsInCluster;

    private String tolerances;

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

    public Double getWeightedMeanM() {
        return weightedMeanM;
    }

    public void setWeightedMeanM(Double weightedMeanM) {
        this.weightedMeanM = weightedMeanM;
    }

    public Double getWeightedStdDevM() {
        return weightedStdDevM;
    }

    public void setWeightedStdDevM(Double weightedStdDevM) {
        this.weightedStdDevM = weightedStdDevM;
    }

    public Double getWeightedMeanC() {
        return weightedMeanC;
    }

    public void setWeightedMeanC(Double weightedMeanC) {
        this.weightedMeanC = weightedMeanC;
    }

    public Double getWeightedStdDevC() {
        return weightedStdDevC;
    }

    public void setWeightedStdDevC(Double weightedStdDevC) {
        this.weightedStdDevC = weightedStdDevC;
    }

    public Long getNumberOfOutliers() {
        return numberOfOutliers;
    }

    public void setNumberOfOutliers(Long numberOfOutliers) {
        this.numberOfOutliers = numberOfOutliers;
    }

    public Long getNumberOfClusters() {
        return numberOfClusters;
    }

    public void setNumberOfClusters(Long numberOfClusters) {
        this.numberOfClusters = numberOfClusters;
    }

    public Double getAvgNumberOfPointsInCluster() {
        return avgNumberOfPointsInCluster;
    }

    public void setAvgNumberOfPointsInCluster(Double avgNumberOfPointsInCluster) {
        this.avgNumberOfPointsInCluster = avgNumberOfPointsInCluster;
    }

    public Double getStdDevAvgNumberOfPointsInCluster() {
        return stdDevAvgNumberOfPointsInCluster;
    }

    public void setStdDevAvgNumberOfPointsInCluster(Double stdDevAvgNumberOfPointsInCluster) {
        this.stdDevAvgNumberOfPointsInCluster = stdDevAvgNumberOfPointsInCluster;
    }

    public String getTolerances() {
        return tolerances;
    }

    public void setTolerances(String tolerances) {
        this.tolerances = tolerances;
    }

    public Double getStdDevR() {
        return stdDevR;
    }

    public void setStdDevR(Double stdDevR) {
        this.stdDevR = stdDevR;
    }

    public Double getMeanR() {
        return meanR;
    }

    public void setMeanR(Double meanR) {
        this.meanR = meanR;
    }

    public static CompiledRegression compiledRegression(int colNo, Iterable<Regression> regressionIterator, Long totalPoints,
                                                        boolean eachPointIsACluster, String tolerances) {
        CompiledRegression compiledRegression = new CompiledRegression();
        compiledRegression.setColNo(colNo);
        compiledRegression.setTolerances(tolerances);

        List<Pair<Double, Double>> list = new ArrayList<>();
        Double mMean = 0.0;
        Double cMean = 0.0;

        Double mMeanWeighted = 0.0;
        Double cMeanWeighted = 0.0;

        List<Double> rList = new ArrayList<>();
        Double rMean = 0.0;

        List<Long> dataPoints = new ArrayList<>();
        Long totalNumOfDataPoints = 0L;
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

            Double r1 = regression.getR1();
            Double r2 = regression.getR2();
            Double r = (r2 == 0.0 ? 0.0 : r1 / r2);

            rList.add(r);
            rMean += r;

            mMeanWeighted += (m * regression.getNumOfDataPoints());
            cMeanWeighted += (c * regression.getNumOfDataPoints());
            totalNumOfDataPoints += regression.getNumOfDataPoints();
            dataPoints.add(regression.getNumOfDataPoints());
        }
        if (list.size() == 0) {
            return compiledRegression;
        }
        mMean = mMean / list.size();
        cMean = cMean / list.size();

        mMeanWeighted /= totalNumOfDataPoints;
        cMeanWeighted /= totalNumOfDataPoints;

        rMean /= list.size();

        Double mStdDev = 0.0;
        Double cStdDev = 0.0;

        Double mStdDevWeighted = 0.0;
        Double cStdDevWeighted = 0.0;

        Double rStdDev = 0.0;

        int tempIndex = 0;

        for (int i = 0; i < list.size(); i++) {
            Pair<Double, Double> elem = list.get(i);
            mStdDev += Math.pow(elem.getKey() - mMean, 2);
            cStdDev += Math.pow(elem.getValue() - cMean, 2);

            mStdDevWeighted += Math.pow((elem.getKey() - mMeanWeighted), 2) * dataPoints.get(tempIndex);
            cStdDevWeighted += Math.pow((elem.getValue() - cMeanWeighted), 2) * dataPoints.get(tempIndex);

            rStdDev += Math.pow(rList.get(i) - rMean, 2);
            tempIndex++;
        }

        mStdDev = Math.sqrt(mStdDev / list.size());
        cStdDev = Math.sqrt(cStdDev / list.size());

        mStdDevWeighted = Math.sqrt(mStdDevWeighted / totalNumOfDataPoints);
        cStdDevWeighted = Math.sqrt(cStdDevWeighted / totalNumOfDataPoints);

        rStdDev = Math.sqrt(rStdDev / rList.size());

        compiledRegression.setMeanM(mMean);
        compiledRegression.setStdDevM(mStdDev);
        compiledRegression.setMeanC(cMean);
        compiledRegression.setStdDevC(cStdDev);

        compiledRegression.setWeightedMeanM(mMeanWeighted);
        compiledRegression.setWeightedStdDevM(mStdDevWeighted);
        compiledRegression.setWeightedMeanC(cMeanWeighted);
        compiledRegression.setWeightedStdDevC(cStdDevWeighted);

        compiledRegression.setMeanR(rMean);
        compiledRegression.setStdDevR(rStdDev);

        if (eachPointIsACluster) {
            compiledRegression.setNumberOfOutliers(totalPoints - dataPoints.size());
        } else {
            compiledRegression.setNumberOfOutliers(totalPoints - totalNumOfDataPoints);
        }
        compiledRegression.setNumberOfClusters((long) dataPoints.size());

        Double avgNumberOfPointsInCluster = totalNumOfDataPoints.doubleValue() / dataPoints.size();
        compiledRegression.setAvgNumberOfPointsInCluster(avgNumberOfPointsInCluster);

        double varianceAvgNumberOfPointsInCluster = dataPoints.stream()
                .mapToDouble(n -> Math.pow(n - avgNumberOfPointsInCluster, 2))
                .average().orElse(0.0);
        compiledRegression.setStdDevAvgNumberOfPointsInCluster(Math.sqrt(varianceAvgNumberOfPointsInCluster));

        return compiledRegression;
    }
}
