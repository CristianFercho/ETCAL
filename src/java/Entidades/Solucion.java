/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author USUARIO
 */
@Entity
@Table(name = "solucion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Solucion.findAll", query = "SELECT s FROM Solucion s"),
    @NamedQuery(name = "Solucion.findByTiquetSolicitud", query = "SELECT s FROM Solucion s WHERE s.tiquetSolicitud = :tiquetSolicitud"),
    @NamedQuery(name = "Solucion.findByDescripcion", query = "SELECT s FROM Solucion s WHERE s.descripcion = :descripcion"),
    @NamedQuery(name = "Solucion.findByFecha", query = "SELECT s FROM Solucion s WHERE s.fecha = :fecha"),
    @NamedQuery(name = "Solucion.findByEjecutada", query = "SELECT s FROM Solucion s WHERE s.ejecutada = :ejecutada")})
public class Solucion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "tiquet_solicitud")
    private String tiquetSolicitud;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "ejecutada")
    private String ejecutada;
    @JoinColumn(name = "tiquet_solicitud", referencedColumnName = "ticket", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Solicitud solicitud;

    public Solucion() {
    }

    public Solucion(String tiquetSolicitud) {
        this.tiquetSolicitud = tiquetSolicitud;
    }

    public Solucion(String tiquetSolicitud, String descripcion, Date fecha, String ejecutada) {
        this.tiquetSolicitud = tiquetSolicitud;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.ejecutada = ejecutada;
    }

    public String getTiquetSolicitud() {
        return tiquetSolicitud;
    }

    public void setTiquetSolicitud(String tiquetSolicitud) {
        this.tiquetSolicitud = tiquetSolicitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getEjecutada() {
        return ejecutada;
    }

    public void setEjecutada(String ejecutada) {
        this.ejecutada = ejecutada;
    }

    public Solicitud getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tiquetSolicitud != null ? tiquetSolicitud.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Solucion)) {
            return false;
        }
        Solucion other = (Solucion) object;
        if ((this.tiquetSolicitud == null && other.tiquetSolicitud != null) || (this.tiquetSolicitud != null && !this.tiquetSolicitud.equals(other.tiquetSolicitud))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Solucion[ tiquetSolicitud=" + tiquetSolicitud + " ]";
    }
    
}
