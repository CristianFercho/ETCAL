/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author USUARIO
 */
@Entity
@Table(name = "ruta")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ruta.findAll", query = "SELECT r FROM Ruta r"),
    @NamedQuery(name = "Ruta.findByNombreRuta", query = "SELECT r FROM Ruta r WHERE r.nombreRuta = :nombreRuta"),
    @NamedQuery(name = "Ruta.findByDescripcion", query = "SELECT r FROM Ruta r WHERE r.descripcion = :descripcion"),
    @NamedQuery(name = "Ruta.findByEstado", query = "SELECT r FROM Ruta r WHERE r.estado = :estado")})
public class Ruta implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "nombre_ruta")
    private String nombreRuta;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "estado")
    private String estado;
    @ManyToMany(mappedBy = "rutaList")
    private List<Estacion> estacionList;

    public Ruta() {
    }

    public Ruta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
    }

    public Ruta(String nombreRuta, String descripcion, String estado) {
        this.nombreRuta = nombreRuta;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public String getNombreRuta() {
        return nombreRuta;
    }

    public void setNombreRuta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @XmlTransient
    public List<Estacion> getEstacionList() {
        return estacionList;
    }

    public void setEstacionList(List<Estacion> estacionList) {
        this.estacionList = estacionList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nombreRuta != null ? nombreRuta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ruta)) {
            return false;
        }
        Ruta other = (Ruta) object;
        if ((this.nombreRuta == null && other.nombreRuta != null) || (this.nombreRuta != null && !this.nombreRuta.equals(other.nombreRuta))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Ruta[ nombreRuta=" + nombreRuta + " ]";
    }
    
}
