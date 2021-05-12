package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {
// esta declarado 0 el saldo dos veces, innecesariamente
  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(double cuanto) {
    esNegativo(cuanto);
    esMayorIgualA3(cuanto);
    Movimiento nuevo = new Movimiento(LocalDate.now(), cuanto, true);
    agregarMovimiento(nuevo);
  }

  public void sacar(double cuanto) {
   esNegativo(cuanto);
   cumpleVerificaciones(cuanto);
   cumpleLimite(cuanto);
   Movimiento nuevo = new Movimiento(LocalDate.now(), cuanto, false);
   agregarMovimiento(nuevo);
  }

  public void agregarMovimiento( Movimiento mov) {
    movimientos.add(mov);
    calcularValor(mov);
  }

  public double calcularValor(Movimiento mov) {
    if (mov.isDeposito()) {
      return saldo += mov.getMonto();
    } else {
      return saldo -= mov.getMonto();
    }
  }

  public void esNegativo(double cuanto){
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
  }
  public void esMayorIgualA3(double cuanto){
    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }
  public void cumpleVerificaciones(double cuanto){
    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }

  }
  public void cumpleLimite(double cuanto){
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;

    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
  }

// en GetMontoExtraido tambien es un Long Method
  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }
//Feature Envy en getMovimientos
  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

}
