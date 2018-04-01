package core.model;

public class Regression {

    private Integer colNo;

    private Long numOfDataPoints;

    private Double m1;
    private Double m2;

    private Double c1;
    private Double c2;

    private Double r1;
    private Double r2;

    public Double getM1() {
        return m1;
    }

    public void setM1(Double m1) {
        this.m1 = m1;
    }

    public Double getM2() {
        return m2;
    }

    public void setM2(Double m2) {
        this.m2 = m2;
    }

    public Double getC1() {
        return c1;
    }

    public void setC1(Double c1) {
        this.c1 = c1;
    }

    public Double getC2() {
        return c2;
    }

    public void setC2(Double c2) {
        this.c2 = c2;
    }

    public Long getNumOfDataPoints() {
        return numOfDataPoints;
    }

    public void setNumOfDataPoints(Long numOfDataPoints) {
        this.numOfDataPoints = numOfDataPoints;
    }

    public Integer getColNo() {
        return colNo;
    }

    public void setColNo(Integer colNo) {
        this.colNo = colNo;
    }

    public Double getR1() {
        return r1;
    }

    public void setR1(Double r1) {
        this.r1 = r1;
    }

    public Double getR2() {
        return r2;
    }

    public void setR2(Double r2) {
        this.r2 = r2;
    }

    public Double getM() {
        return (m2 == 0.0 ? 0.0 : m1 / m2);
    }

    public Double getC() {
        return (c2 == 0.0 ? 0.0 : c1 / c2);
    }

    public Double getR() {
        return (r2 == 0.0 ? 0.0 : r1 / r2);
    }

    public Double getRSquared() {
        Double r = (r2 == 0.0 ? 0.0 : r1 / r2);
        return r * r;
    }
}
