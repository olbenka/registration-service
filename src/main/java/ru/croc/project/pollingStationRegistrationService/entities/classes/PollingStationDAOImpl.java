package ru.croc.project.pollingStationRegistrationService.entities.classes;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import ru.croc.project.pollingStationRegistrationService.entities.PollingStation;
import org.springframework.jdbc.core.JdbcTemplate;


import java.util.Collections;
import java.util.List;

@Repository
public class PollingStationDAOImpl implements PollingStationDAO {
    private final JdbcTemplate jdbcTemplate;

    public PollingStationDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void registerPollingStation(PollingStation pollingStation) {
        try {
            String sql = "INSERT INTO polling_station_table (station_id, client_id, address, capacity, urn_count," +
                    "has_special_equipment, working_hours, phone_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,pollingStation.getStationId(), pollingStation.getClientId(), pollingStation.getAddress(),
                    pollingStation.getCapacity(), pollingStation.getUrnCount(), pollingStation.isHasSpecialEquipment(),
                    pollingStation.getWorkingHours(), pollingStation.getPhoneNumber());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deletePollingStation(long chatId, long stationId) {
        try {
            String sql = "DELETE FROM polling_station_table WHERE client_id = ? AND station_id = ?";
            jdbcTemplate.update(sql, chatId, stationId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PollingStation> getAllPollingStations(long chatId) {
        try {
            String sql = "SELECT * FROM polling_station_table WHERE client_id = ?";
            List<PollingStation> result = jdbcTemplate.query(sql, new Object[]{chatId}, new BeanPropertyRowMapper<>(PollingStation.class));

            for (PollingStation station : result) {
                System.out.println("Fetched PollingStation: " + station);
            }

            return result;
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public PollingStation getPollingStationById(long id) {
        try {
            String sql = "SELECT * FROM polling_station_table WHERE station_id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(PollingStation.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

}
