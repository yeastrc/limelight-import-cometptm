package org.yeastrc.limelight.xml.cometptm.objects;

import java.math.BigDecimal;
import java.util.Map;

public class CometPSM {

	private Boolean isDecoy;
	private BigDecimal evalue;
	private BigDecimal xcorr;
	private BigDecimal deltaCn;
	private BigDecimal deltaCnStar;
	private BigDecimal spscore;
	private BigDecimal sprank;
	private BigDecimal massDiff;
	private int hitRank;
	private int scanNumber;
	private BigDecimal precursorNeutralMass;
	private int charge;
	private BigDecimal retentionTime;
	private String peptideSequence;
	private Map<Integer,BigDecimal> modifications;


	public Boolean getDecoy() {
		return isDecoy;
	}

	public void setDecoy(Boolean decoy) {
		isDecoy = decoy;
	}

	public BigDecimal getEvalue() {
		return evalue;
	}

	public void setEvalue(BigDecimal evalue) {
		this.evalue = evalue;
	}

	public BigDecimal getXcorr() {
		return xcorr;
	}

	public void setXcorr(BigDecimal xcorr) {
		this.xcorr = xcorr;
	}

	public BigDecimal getDeltaCn() {
		return deltaCn;
	}

	public void setDeltaCn(BigDecimal deltaCn) {
		this.deltaCn = deltaCn;
	}

	public BigDecimal getDeltaCnStar() {
		return deltaCnStar;
	}

	public void setDeltaCnStar(BigDecimal deltaCnStar) {
		this.deltaCnStar = deltaCnStar;
	}

	public BigDecimal getSpscore() {
		return spscore;
	}

	public void setSpscore(BigDecimal spscore) {
		this.spscore = spscore;
	}

	public BigDecimal getSprank() {
		return sprank;
	}

	public void setSprank(BigDecimal sprank) {
		this.sprank = sprank;
	}

	public BigDecimal getMassDiff() {
		return massDiff;
	}

	public void setMassDiff(BigDecimal massDiff) {
		this.massDiff = massDiff;
	}

	public int getHitRank() {
		return hitRank;
	}

	public void setHitRank(int hitRank) {
		this.hitRank = hitRank;
	}

	public int getScanNumber() {
		return scanNumber;
	}

	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}

	public BigDecimal getPrecursorNeutralMass() {
		return precursorNeutralMass;
	}

	public void setPrecursorNeutralMass(BigDecimal precursorNeutralMass) {
		this.precursorNeutralMass = precursorNeutralMass;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public BigDecimal getRetentionTime() {
		return retentionTime;
	}

	public void setRetentionTime(BigDecimal retentionTime) {
		this.retentionTime = retentionTime;
	}

	public String getPeptideSequence() {
		return peptideSequence;
	}

	public void setPeptideSequence(String peptideSequence) {
		this.peptideSequence = peptideSequence;
	}

	public Map<Integer, BigDecimal> getModifications() {
		return modifications;
	}

	public void setModifications(Map<Integer, BigDecimal> modifications) {
		this.modifications = modifications;
	}

}
