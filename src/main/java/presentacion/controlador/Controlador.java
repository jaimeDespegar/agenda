package presentacion.controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import modelo.Agenda;
import presentacion.reportes.ReporteAgenda;
import presentacion.vista.VentanaPersona;
import presentacion.vista.Vista;
import dto.PersonaDTO;

public class Controlador implements ActionListener
{
		private Vista vista;
		private List<PersonaDTO> personas_en_tabla;
		private VentanaPersona ventanaPersona; 
		private Agenda agenda;
		private Map<Object, Runnable> mapaAcciones;
		private Logger log = LoggerFactory.getLogger(Controlador.class);
		
		public Controlador(Vista vista, Agenda agenda)
		{
			this.vista = vista;
			this.vista.getBtnAgregar().addActionListener(this);
			this.vista.getBtnBorrar().addActionListener(this);
			this.vista.getBtnReporte().addActionListener(this);
			this.agenda = agenda;
			this.personas_en_tabla = null;
			this.mapaAcciones = new HashMap<>();
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			this.mapaAcciones.getOrDefault(e.getSource(), () -> log.warn("No existe ninguna acciona asociada a este evento!"))
							 .run();
		}
		
		public void inicializar() {
			this.llenarTabla();
			this.cargarAcciones();
			this.vista.show();
		}
		
		private void cargarAcciones() 
		{
			this.mapaAcciones.put(this.vista.getBtnAgregar(), () -> this.abrirVentanaPersonaNueva());
			this.mapaAcciones.put(this.vista.getBtnBorrar(), () -> this.borrarPersonas());
			this.mapaAcciones.put(this.vista.getBtnReporte(), () -> this.generarReporte());
		}

		private void llenarTabla()
		{
			this.vista.vaciarTablaPersonas();
			this.personas_en_tabla = agenda.obtenerPersonas();
			this.personas_en_tabla.forEach(p -> agregarPersonaALaTabla(p) );
		}
		
		private void agregarPersonaALaTabla(PersonaDTO persona) {
			Object[] fila = { persona.getNombre(), 
							  persona.getTelefono() };
			this.vista.agregarFilaAlaTabla(fila);
		}

		private void abrirVentanaPersonaNueva() {
			this.ventanaPersona = new VentanaPersona(this);			
			this.mapaAcciones.put(ventanaPersona.getBtnAgregarPersona(), ()-> this.agregarPersonaNueva());
			// FIXME la key se agrega cada vez que agrego una persona, una nueva venta genera un nuevo boton. Arreglarlo!
		}
		
		private void agregarPersonaNueva() {
			PersonaDTO nuevaPersona = new PersonaDTO(0,this.ventanaPersona.getTxtNombre().getText(), ventanaPersona.getTxtTelefono().getText());
			this.agenda.agregarPersona(nuevaPersona);
			this.llenarTabla();
			this.ventanaPersona.dispose();
		}
		
		private void borrarPersonas() {
			int[] filas_seleccionadas = this.vista.getFilasSeleccionadas();
			for (int fila : filas_seleccionadas)
			{
				this.agenda.borrarPersona(this.personas_en_tabla.get(fila));
			}
			this.llenarTabla();
		}
		
		private void generarReporte() {
			ReporteAgenda reporte = new ReporteAgenda(agenda.obtenerPersonas());
			reporte.mostrar();				
		}

}