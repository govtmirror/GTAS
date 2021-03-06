/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import java.util.Date;

public class CaseVo {
    private Long passengerId;
    private Long flightId;
    private String hitType;
    private Date flightEta;
    private Date flightEtd;
    private String flightDirection;
	private String lastName;
    private String firstName;
    private String middleName;
    private String flightNumber;
    private String createDate; 
    private String status;
    
    public String getHitType() {
		return hitType;
	}
	public void setHitType(String hitType) {
		this.hitType = hitType;
	}
	public Date getFlightEta() {
		return flightEta;
	}
	public void setFlightEta(Date flightEta) {
		this.flightEta = flightEta;
	}
	public Date getFlightEtd() {
		return flightEtd;
	}
	public void setFlightEtd(Date flightEtd) {
		this.flightEtd = flightEtd;
	}
	public Long getPassengerId() {
        return passengerId;
    }
    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }
    public Long getFlightId() {
        return flightId;
    }
    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getMiddleName() {
        return middleName;
    }
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    public String getFlightNumber() {
        return flightNumber;
    }
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    public String getCreateDate() {
        return createDate;
    }
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
	public String getFlightDirection() {
		return flightDirection;
	}
	public void setFlightDirection(String flightDirection) {
		this.flightDirection = flightDirection;
	}
}
