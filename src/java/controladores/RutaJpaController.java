/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Estacion;
import Entidades.Ruta;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import controladores.exceptions.RollbackFailureException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author USUARIO
 */
public class RutaJpaController implements Serializable {

    public RutaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ruta ruta) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (ruta.getEstacionList() == null) {
            ruta.setEstacionList(new ArrayList<Estacion>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Estacion> attachedEstacionList = new ArrayList<Estacion>();
            for (Estacion estacionListEstacionToAttach : ruta.getEstacionList()) {
                estacionListEstacionToAttach = em.getReference(estacionListEstacionToAttach.getClass(), estacionListEstacionToAttach.getNombreEstacion());
                attachedEstacionList.add(estacionListEstacionToAttach);
            }
            ruta.setEstacionList(attachedEstacionList);
            em.persist(ruta);
            for (Estacion estacionListEstacion : ruta.getEstacionList()) {
                estacionListEstacion.getRutaList().add(ruta);
                estacionListEstacion = em.merge(estacionListEstacion);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findRuta(ruta.getNombreRuta()) != null) {
                throw new PreexistingEntityException("Ruta " + ruta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ruta ruta) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ruta persistentRuta = em.find(Ruta.class, ruta.getNombreRuta());
            List<Estacion> estacionListOld = persistentRuta.getEstacionList();
            List<Estacion> estacionListNew = ruta.getEstacionList();
            List<Estacion> attachedEstacionListNew = new ArrayList<Estacion>();
            for (Estacion estacionListNewEstacionToAttach : estacionListNew) {
                estacionListNewEstacionToAttach = em.getReference(estacionListNewEstacionToAttach.getClass(), estacionListNewEstacionToAttach.getNombreEstacion());
                attachedEstacionListNew.add(estacionListNewEstacionToAttach);
            }
            estacionListNew = attachedEstacionListNew;
            ruta.setEstacionList(estacionListNew);
            ruta = em.merge(ruta);
            for (Estacion estacionListOldEstacion : estacionListOld) {
                if (!estacionListNew.contains(estacionListOldEstacion)) {
                    estacionListOldEstacion.getRutaList().remove(ruta);
                    estacionListOldEstacion = em.merge(estacionListOldEstacion);
                }
            }
            for (Estacion estacionListNewEstacion : estacionListNew) {
                if (!estacionListOld.contains(estacionListNewEstacion)) {
                    estacionListNewEstacion.getRutaList().add(ruta);
                    estacionListNewEstacion = em.merge(estacionListNewEstacion);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = ruta.getNombreRuta();
                if (findRuta(id) == null) {
                    throw new NonexistentEntityException("The ruta with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ruta ruta;
            try {
                ruta = em.getReference(Ruta.class, id);
                ruta.getNombreRuta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ruta with id " + id + " no longer exists.", enfe);
            }
            List<Estacion> estacionList = ruta.getEstacionList();
            for (Estacion estacionListEstacion : estacionList) {
                estacionListEstacion.getRutaList().remove(ruta);
                estacionListEstacion = em.merge(estacionListEstacion);
            }
            em.remove(ruta);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ruta> findRutaEntities() {
        return findRutaEntities(true, -1, -1);
    }

    public List<Ruta> findRutaEntities(int maxResults, int firstResult) {
        return findRutaEntities(false, maxResults, firstResult);
    }

    private List<Ruta> findRutaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ruta.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Ruta findRuta(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ruta.class, id);
        } finally {
            em.close();
        }
    }

    public int getRutaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ruta> rt = cq.from(Ruta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
