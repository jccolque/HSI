package net.pladema.establishment.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import net.pladema.establishment.controller.dto.BedDto;
import net.pladema.establishment.controller.dto.RoomDto;
import net.pladema.establishment.controller.mapper.BedMapper;
import net.pladema.establishment.controller.mapper.RoomMapper;
import net.pladema.establishment.repository.BedRepository;
import net.pladema.establishment.repository.RoomRepository;
import net.pladema.establishment.repository.entity.Bed;
import net.pladema.establishment.repository.entity.Room;

@RestController
@Api(value = "Room", tags = { "Room" })
@RequestMapping("/room")
public class RoomController {

	private static final Logger LOG = LoggerFactory.getLogger(RoomController.class);

	private RoomRepository roomRepository;

	private RoomMapper roomMapper;
	
	private BedRepository bedRepository;

	private BedMapper bedMapper;


	public RoomController(RoomRepository roomRepository, RoomMapper roomMapper, BedRepository bedRepository,
			BedMapper bedMapper) {
		this.roomRepository = roomRepository;
		this.roomMapper = roomMapper;
		this.bedRepository = bedRepository;
		this.bedMapper = bedMapper;
	}

	@GetMapping()
	public ResponseEntity<List<RoomDto>> getAll() {
		List<Room> rooms = roomRepository.findAll();
		LOG.debug("Get all Rooms => {}", rooms);
		return ResponseEntity.ok(roomMapper.toListRoomDto(rooms));
	}

	@GetMapping("/{roomId}/beds")
	public ResponseEntity<List<BedDto>> getAllBedsByRoom(@PathVariable(name = "roomId")  Integer roomId) {
		List<Bed> beds = bedRepository.getAllByRoom(roomId);
		LOG.debug("Get all Beds => {}", beds);
		return ResponseEntity.ok(bedMapper.toListBedDto(beds));
	}

	@GetMapping("/{roomId}/freebeds")
	public ResponseEntity<List<BedDto>> getAllFreeBedsByRoom(@PathVariable(name = "roomId")  Integer roomId) {
		List<Bed> beds = bedRepository.getAllFreeBedsByRoom(roomId);
		LOG.debug("Get all free Beds  => {}", beds);
		return ResponseEntity.ok(bedMapper.toListBedDto(beds));
	}
}
