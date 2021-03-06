/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGES_ADMIN_AND_VIEW_FLIGHT_PASSENGER;
import gov.gtas.model.Disposition;
import gov.gtas.model.HitsSummary;
import gov.gtas.model.Passenger;
import gov.gtas.model.User;
import gov.gtas.model.lookup.DispositionStatus;
import gov.gtas.services.dto.PassengersPageDto;
import gov.gtas.services.dto.PassengersRequestDto;
import gov.gtas.vo.passenger.CaseVo;
import gov.gtas.vo.passenger.PassengerVo;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

public interface PassengerService {
	public Passenger create(Passenger passenger);

	public Passenger update(Passenger passenger);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_FLIGHT_PASSENGER)
	public Passenger findById(Long id);

	public List<Passenger> getPassengersByLastName(String lastName);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_FLIGHT_PASSENGER)
	public List<Disposition> getPassengerDispositionHistory(Long passengerId,
			Long flightId);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_FLIGHT_PASSENGER)
	public void createDisposition(DispositionData disposition, User user);

	public void createDisposition(HitsSummary hit);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_FLIGHT_PASSENGER)
	public List<DispositionStatus> getDispositionStatuses();

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_FLIGHT_PASSENGER)
	public List<CaseVo> getAllDispositions();

	public void createOrEditDispositionStatus(DispositionStatus ds);

	public void deleteDispositionStatus(DispositionStatus ds);

	/**
	 * 
	 * @param flightId
	 *            optional
	 * @param request
	 * @return
	 */
	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_FLIGHT_PASSENGER)
	public PassengersPageDto getPassengersByCriteria(Long flightId,
			PassengersRequestDto request);

	public void fillWithHitsInfo(PassengerVo vo, Long flightId, Long passengerId);
}
