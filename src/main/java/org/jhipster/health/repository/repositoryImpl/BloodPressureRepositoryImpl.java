package org.jhipster.health.repository.repositoryImpl;

import org.jhipster.health.domain.BloodPressure;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class BloodPressureRepositoryImpl  {

    @PersistenceContext
    protected EntityManager entityManager;

    public void updateBloodPressure(BloodPressure bloodPressure) {
        String sql = "UPDATE BLOOD_PRESSURE SET systolic = " + bloodPressure.getSystolic() + ", diastolic = "+ bloodPressure.getDiastolic() +" WHERE id = " + bloodPressure.getId();
        System.out.println(sql);
        Query query = entityManager.createNativeQuery(sql);
        query.executeUpdate();
    }
}
