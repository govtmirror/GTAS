/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.enumtype.AuditActionType;
import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.enumtype.Status;
import gov.gtas.json.AuditActionData;
import gov.gtas.json.AuditActionTarget;
import gov.gtas.model.AuditRecord;
import gov.gtas.model.Disposition;
import gov.gtas.model.Flight;
import gov.gtas.model.HitsSummary;
import gov.gtas.model.Passenger;
import gov.gtas.model.User;
import gov.gtas.model.lookup.DispositionStatus;
import gov.gtas.repository.AuditRecordRepository;
import gov.gtas.repository.DispositionRepository;
import gov.gtas.repository.DispositionStatusRepository;
import gov.gtas.repository.HitsSummaryRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.services.dto.PassengersPageDto;
import gov.gtas.services.dto.PassengersRequestDto;
import gov.gtas.services.security.UserService;
import gov.gtas.services.security.UserServiceUtil;
import gov.gtas.vo.passenger.CaseVo;
import gov.gtas.vo.passenger.PassengerVo;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * The Class PassengerServiceImpl.
 */
@Service
public class PassengerServiceImpl implements PassengerService {

	private static final Logger logger = LoggerFactory
			.getLogger(PassengerServiceImpl.class);

	@Resource
	private PassengerRepository passengerRespository;

	@Resource
	private HitsSummaryRepository hitsSummaryRepository;

	@Resource
	private DispositionStatusRepository dispositionStatusRepo;

	@Resource
	private DispositionRepository dispositionRepo;

	@Autowired
	private AuditRecordRepository auditLogRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private UserServiceUtil userServiceUtil;

	@Override
	@Transactional
	public Passenger create(Passenger passenger) {
		return passengerRespository.save(passenger);
	}

	@Override
	@Transactional
	public PassengersPageDto getPassengersByCriteria(Long flightId,
			PassengersRequestDto request) {
		List<PassengerVo> rv = new ArrayList<>();
		Pair<Long, List<Object[]>> tuple = passengerRespository.findByCriteria(
				flightId, request);
		int count = 0;
		for (Object[] objs : tuple.getRight()) {
			if (count == request.getPageSize()) {
				break;
			}

			Passenger p = (Passenger) objs[0];
			Flight f = (Flight) objs[1];
			HitsSummary hit = (HitsSummary) objs[2];

			if (hit != null && f.getId() != hit.getFlight().getId()) {
				continue;
			}

			PassengerVo vo = new PassengerVo();
			BeanUtils.copyProperties(p, vo);
			rv.add(vo);
			count++;

			if (hit != null) {
				String hitType = hit.getHitType();
				if (hitType.contains(HitTypeEnum.R.toString())) {
					vo.setOnRuleHitList(true);
				}
				if (hitType.contains(HitTypeEnum.P.toString())) {
					vo.setOnWatchList(true);
				}
				if (hitType.contains(HitTypeEnum.D.toString())) {
					vo.setOnWatchListDoc(true);
				}
			}

			// grab flight info
			vo.setFlightId(f.getId().toString());
			vo.setFlightNumber(f.getFlightNumber());
			vo.setFullFlightNumber(f.getFullFlightNumber());
			vo.setCarrier(f.getCarrier());
			vo.setEtd(f.getEtd());
			vo.setEta(f.getEta());
		}

		return new PassengersPageDto(rv, tuple.getLeft());
	}

	@Override
	@Transactional
	public List<CaseVo> getAllDispositions() {
		List<CaseVo> rv = new ArrayList<>();
		List<Object[]> cases = passengerRespository.findAllDispositions();
		for (Object[] objs : cases) {
			CaseVo vo = new CaseVo();
			rv.add(vo);
			vo.setPassengerId(((BigInteger) objs[0]).longValue());
			vo.setFlightId(((BigInteger) objs[1]).longValue());
			vo.setFirstName((String) objs[2]);
			vo.setLastName((String) objs[3]);
			vo.setMiddleName((String) objs[4]);
			vo.setFlightNumber((String) objs[5]);

			Timestamp ts = (Timestamp) objs[6];
			String datetime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
					.format(ts);
			vo.setCreateDate(datetime);

			vo.setStatus((String) objs[7]);
		}

		return rv;
	}

	@Override
	@Transactional
	public Passenger update(Passenger passenger) {
		Passenger passengerToUpdate = this.findById(passenger.getId());
		if (passengerToUpdate != null) {
			passengerToUpdate.setAge(passenger.getAge());
			passengerToUpdate.setCitizenshipCountry(passenger
					.getCitizenshipCountry());
			passengerToUpdate.setDebarkation(passenger.getDebarkation());
			passengerToUpdate.setDebarkCountry(passenger.getDebarkCountry());
			passengerToUpdate.setDob(passenger.getDob());
			passengerToUpdate.setEmbarkation(passenger.getEmbarkation());
			passengerToUpdate.setEmbarkCountry(passenger.getEmbarkCountry());
			passengerToUpdate.setFirstName(passenger.getFirstName());
			passengerToUpdate.setFlights(passenger.getFlights());
			passengerToUpdate.setGender(passenger.getGender());
			passengerToUpdate.setLastName(passenger.getLastName());
			passengerToUpdate.setMiddleName(passenger.getMiddleName());
			passengerToUpdate.setResidencyCountry(passenger
					.getResidencyCountry());
			passengerToUpdate.setDocuments(passenger.getDocuments());
			passengerToUpdate.setSuffix(passenger.getSuffix());
			passengerToUpdate.setTitle(passenger.getTitle());
		}
		return passengerToUpdate;
	}

	@Override
	@Transactional
	public List<Disposition> getPassengerDispositionHistory(Long passengerId,
			Long flightId) {
		return passengerRespository.getPassengerDispositionHistory(passengerId,
				flightId);
	}

	@Override
	public List<DispositionStatus> getDispositionStatuses() {
		Iterable<DispositionStatus> i = dispositionStatusRepo.findAll();
		if (i != null) {
			return IteratorUtils.toList(i.iterator());
		}
		return new ArrayList<>();
	}

	@Transactional
	@Override
	public void createOrEditDispositionStatus(DispositionStatus ds) {
		dispositionStatusRepo.save(ds);
	}

	@Transactional
	@Override
	public void deleteDispositionStatus(DispositionStatus ds) {
		dispositionStatusRepo.delete(ds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.services.PassengerService#createDisposition(gov.gtas.services
	 * .DispositionData)
	 */
	@Transactional
	@Override
	public void createDisposition(DispositionData disposition, User loggedinUser) {
		Disposition d = new Disposition();
		d.setCreatedAt(new Date());
		d.setCreatedBy(disposition.getUser());
		Flight f = new Flight();
		f.setId(disposition.getFlightId());
		d.setFlight(f);
		Passenger p = new Passenger();
		p.setId(disposition.getPassengerId());
		d.setPassenger(p);
		d.setComments(disposition.getComments());
		DispositionStatus status = new DispositionStatus();
		status.setId(disposition.getStatusId());
		d.setStatus(status);

		dispositionRepo.save(d);
		writeAuditLogForDisposition(disposition.getPassengerId(), loggedinUser);
	}

	/**
	 * Write audit log for disposition.
	 *
	 * @param passengerId
	 *            the passenger id
	 */
	private void writeAuditLogForDisposition(Long pId, User loggedinUser) {
		Passenger passenger = findById(pId);
		try {
			AuditActionTarget target = new AuditActionTarget(passenger);
			AuditActionData actionData = new AuditActionData();

			actionData.addProperty("CitizenshipCountry",
					passenger.getCitizenshipCountry());
			actionData.addProperty("PassengerType",
					passenger.getPassengerType());
			//
			String message = "Disposition Status Change run on "
					+ passenger.getCreatedAt();
			auditLogRepository.save(new AuditRecord(
					AuditActionType.DISPOSITION_STATUS_CHANGE, target
							.toString(), Status.SUCCESS, message, actionData
							.toString(), loggedinUser, new Date()));

		} catch (Exception ex) {
			logger.warn(ex.getMessage());
		}
	}

	@Override
	public void createDisposition(HitsSummary hit) {
		Disposition d = new Disposition();
		Date date = new Date();
		d.setCreatedAt(date);
		d.setCreatedBy("SYSTEM");
		d.setComments("A new disposition has been created on " + date);
		d.setPassenger(hit.getPassenger());
		d.setFlight(hit.getFlight());
		DispositionStatus status = new DispositionStatus();
		status.setId(1L);
		d.setStatus(status);

		dispositionRepo.save(d);
	}

	@Override
	@Transactional
	public Passenger findById(Long id) {
		return passengerRespository.findOne(id);
	}

	@Override
	@Transactional
	public List<Passenger> getPassengersByLastName(String lastName) {
		return passengerRespository.getPassengersByLastName(lastName);
	}

	@Override
	public void fillWithHitsInfo(PassengerVo vo, Long flightId, Long passengerId) {
		List<HitsSummary> hitsSummary = hitsSummaryRepository
				.findByFlightIdAndPassengerId(flightId, passengerId);
		if (!CollectionUtils.isEmpty(hitsSummary)) {
			for (HitsSummary hs : hitsSummary) {
				String hitType = hs.getHitType();
				if (hitType.contains(HitTypeEnum.R.toString())) {
					vo.setOnRuleHitList(true);
				}
				if (hitType.contains(HitTypeEnum.P.toString())) {
					vo.setOnWatchList(true);
				}
				if (hitType.contains(HitTypeEnum.D.toString())) {
					vo.setOnWatchListDoc(true);
				}
			}
		}
	}
}