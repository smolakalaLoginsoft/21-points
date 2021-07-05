package org.jhipster.health.repository.repositoryImpl;

import org.jhipster.health.domain.Points;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Component
@Transactional
public class PointsRepositoryImpl {

    @PersistenceContext
    protected EntityManager entityManager;

    public void updatePoints(Points points) {
        String sql = "UPDATE points SET notes = '" + points.getNotes() +"' WHERE id = " + points.getId();
        System.out.println(sql);
        Query query = entityManager.createNativeQuery(sql);
        query.executeUpdate();
    }
}
