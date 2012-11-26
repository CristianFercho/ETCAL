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
import Entidades.Solucion;
import Entidades.Usuario;
import Entidades.Estacion;
import Entidades.Solicitud;
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
public class SolicitudJpaController implements Serializable {

    public SolicitudJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Solicitud solicitud) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Solucion solucion = solicitud.getSolucion();
            if (solucion != null) {
                solucion = em.getReference(solucion.getClass(), solucion.getTiquetSolicitud());
                solicitud.setSolucion(solucion);
            }
            Usuario idUsuario = solicitud.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdUsuario());
                solicitud.setIdUsuario(idUsuario);
            }
            Estacion nombreEstacion = solicitud.getNombreEstacion();
            if (nombreEstacion != null) {
                nombreEstacion = em.getReference(nombreEstacion.getClass(), nombreEstacion.getNombreEstacion());
                solicitud.setNombreEstacion(nombreEstacion);
            }
            em.persist(solicitud);
            if (solucion != null) {
                Solicitud oldSolicitudOfSolucion = solucion.getSolicitud();
                if (oldSolicitudOfSolucion != null) {
                    oldSolicitudOfSolucion.setSolucion(null);
                    oldSolicitudOfSolucion = em.merge(oldSolicitudOfSolucion);
                }
                solucion.setSolicitud(solicitud);
                solucion = em.merge(solucion);
            }
            if (idUsuario != null) {
                idUsuario.getSolicitudList().add(solicitud);
                idUsuario = em.merge(idUsuario);
            }
            if (nombreEstacion != null) {
                nombreEstacion.getSolicitudList().add(solicitud);
                nombreEstacion = em.merge(nombreEstacion);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findSolicitud(solicitud.getTicket()) != null) {
                throw new PreexistingEntityException("Solicitud " + solicitud + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Solicitud solicitud) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Solicitud persistentSolicitud = em.find(Solicitud.class, solicitud.getTicket());
            Solucion solucionOld = persistentSolicitud.getSolucion();
            Solucion solucionNew = solicitud.getSolucion();
            Usuario idUsuarioOld = persistentSolicitud.getIdUsuario();
            Usuario idUsuarioNew = solicitud.getIdUsuario();
            Estacion nombreEstacionOld = persistentSolicitud.getNombreEstacion();
            Estacion nombreEstacionNew = solicitud.getNombreEstacion();
            List<String> illegalOrphanMessages = null;
            if (solucionOld != null && !solucionOld.equals(solucionNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Solucion " + solucionOld + " since its solicitud field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (solucionNew != null) {
                solucionNew = em.getReference(solucionNew.getClass(), solucionNew.getTiquetSolicitud());
                solicitud.setSolucion(solucionNew);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                solicitud.setIdUsuario(idUsuarioNew);
            }
            if (nombreEstacionNew != null) {
                nombreEstacionNew = em.getReference(nombreEstacionNew.getClass(), nombreEstacionNew.getNombreEstacion());
                solicitud.setNombreEstacion(nombreEstacionNew);
            }
            solicitud = em.merge(solicitud);
            if (solucionNew != null && !solucionNew.equals(solucionOld)) {
                Solicitud oldSolicitudOfSolucion = solucionNew.getSolicitud();
                if (oldSolicitudOfSolucion != null) {
                    oldSolicitudOfSolucion.setSolucion(null);
                    oldSolicitudOfSolucion = em.merge(oldSolicitudOfSolucion);
                }
                solucionNew.setSolicitud(solicitud);
                solucionNew = em.merge(solucionNew);
            }
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getSolicitudList().remove(solicitud);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getSolicitudList().add(solicitud);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            if (nombreEstacionOld != null && !nombreEstacionOld.equals(nombreEstacionNew)) {
                nombreEstacionOld.getSolicitudList().remove(solicitud);
                nombreEstacionOld = em.merge(nombreEstacionOld);
            }
            if (nombreEstacionNew != null && !nombreEstacionNew.equals(nombreEstacionOld)) {
                nombreEstacionNew.getSolicitudList().add(solicitud);
                nombreEstacionNew = em.merge(nombreEstacionNew);
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
                String id = solicitud.getTicket();
                if (findSolicitud(id) == null) {
                    throw new NonexistentEntityException("The solicitud with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Solicitud solicitud;
            try {
                solicitud = em.getReference(Solicitud.class, id);
                solicitud.getTicket();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The solicitud with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Solucion solucionOrphanCheck = solicitud.getSolucion();
            if (solucionOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Solicitud (" + solicitud + ") cannot be destroyed since the Solucion " + solucionOrphanCheck + " in its solucion field has a non-nullable solicitud field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario idUsuario = solicitud.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getSolicitudList().remove(solicitud);
                idUsuario = em.merge(idUsuario);
            }
            Estacion nombreEstacion = solicitud.getNombreEstacion();
            if (nombreEstacion != null) {
                nombreEstacion.getSolicitudList().remove(solicitud);
                nombreEstacion = em.merge(nombreEstacion);
            }
            em.remove(solicitud);
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

    public List<Solicitud> findSolicitudEntities() {
        return findSolicitudEntities(true, -1, -1);
    }

    public List<Solicitud> findSolicitudEntities(int maxResults, int firstResult) {
        return findSolicitudEntities(false, maxResults, firstResult);
    }

    private List<Solicitud> findSolicitudEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Solicitud.class));
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

    public Solicitud findSolicitud(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Solicitud.class, id);
        } finally {
            em.close();
        }
    }

    public int getSolicitudCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Solicitud> rt = cq.from(Solicitud.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
