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
import Entidades.Solicitud;
import Entidades.Solucion;
import controladores.exceptions.IllegalOrphanException;
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
public class SolucionJpaController implements Serializable {

    public SolucionJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Solucion solucion) throws IllegalOrphanException, PreexistingEntityException, RollbackFailureException, Exception {
        List<String> illegalOrphanMessages = null;
        Solicitud solicitudOrphanCheck = solucion.getSolicitud();
        if (solicitudOrphanCheck != null) {
            Solucion oldSolucionOfSolicitud = solicitudOrphanCheck.getSolucion();
            if (oldSolucionOfSolicitud != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Solicitud " + solicitudOrphanCheck + " already has an item of type Solucion whose solicitud column cannot be null. Please make another selection for the solicitud field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Solicitud solicitud = solucion.getSolicitud();
            if (solicitud != null) {
                solicitud = em.getReference(solicitud.getClass(), solicitud.getTicket());
                solucion.setSolicitud(solicitud);
            }
            em.persist(solucion);
            if (solicitud != null) {
                solicitud.setSolucion(solucion);
                solicitud = em.merge(solicitud);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findSolucion(solucion.getTiquetSolicitud()) != null) {
                throw new PreexistingEntityException("Solucion " + solucion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Solucion solucion) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Solucion persistentSolucion = em.find(Solucion.class, solucion.getTiquetSolicitud());
            Solicitud solicitudOld = persistentSolucion.getSolicitud();
            Solicitud solicitudNew = solucion.getSolicitud();
            List<String> illegalOrphanMessages = null;
            if (solicitudNew != null && !solicitudNew.equals(solicitudOld)) {
                Solucion oldSolucionOfSolicitud = solicitudNew.getSolucion();
                if (oldSolucionOfSolicitud != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Solicitud " + solicitudNew + " already has an item of type Solucion whose solicitud column cannot be null. Please make another selection for the solicitud field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (solicitudNew != null) {
                solicitudNew = em.getReference(solicitudNew.getClass(), solicitudNew.getTicket());
                solucion.setSolicitud(solicitudNew);
            }
            solucion = em.merge(solucion);
            if (solicitudOld != null && !solicitudOld.equals(solicitudNew)) {
                solicitudOld.setSolucion(null);
                solicitudOld = em.merge(solicitudOld);
            }
            if (solicitudNew != null && !solicitudNew.equals(solicitudOld)) {
                solicitudNew.setSolucion(solucion);
                solicitudNew = em.merge(solicitudNew);
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
                String id = solucion.getTiquetSolicitud();
                if (findSolucion(id) == null) {
                    throw new NonexistentEntityException("The solucion with id " + id + " no longer exists.");
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
            Solucion solucion;
            try {
                solucion = em.getReference(Solucion.class, id);
                solucion.getTiquetSolicitud();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The solucion with id " + id + " no longer exists.", enfe);
            }
            Solicitud solicitud = solucion.getSolicitud();
            if (solicitud != null) {
                solicitud.setSolucion(null);
                solicitud = em.merge(solicitud);
            }
            em.remove(solucion);
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

    public List<Solucion> findSolucionEntities() {
        return findSolucionEntities(true, -1, -1);
    }

    public List<Solucion> findSolucionEntities(int maxResults, int firstResult) {
        return findSolucionEntities(false, maxResults, firstResult);
    }

    private List<Solucion> findSolucionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Solucion.class));
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

    public Solucion findSolucion(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Solucion.class, id);
        } finally {
            em.close();
        }
    }

    public int getSolucionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Solucion> rt = cq.from(Solucion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
