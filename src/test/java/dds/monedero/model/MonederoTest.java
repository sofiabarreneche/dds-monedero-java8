package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MonederoTest {
  private Cuenta cuenta;
  private final LocalDate fecha = LocalDate.of(2020,7,31);
  private final Movimiento mov = new Movimiento(fecha,300,true);

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  public void ListaDeMovimientosContieneMovimientoReciente(){
    cuenta.agregarMovimiento(mov);
    assertTrue(cuenta.getMovimientos().contains(mov));
  }

  @Test
  public void ElMovimientoAgregadoLoReconoce(){
    cuenta.setSaldo(0);
    cuenta.agregarMovimiento(mov);
    assertEquals(cuenta.getMontoExtraidoA(fecha),300);
  }

  @Test
  void PonerDineroEnLaCuenta() {
    cuenta.poner(1500);
    assertEquals(cuenta.getSaldo(),1500);
  }

  @Test
  void PonerMontoNegativo() {

    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  void TresDepositos() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);
    assertEquals(cuenta.getSaldo(),3856);
  }

  @Test
  void MasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.poner(1500);
          cuenta.poner(456);
          cuenta.poner(1900);
          cuenta.poner(245);
    });
  }

  @Test
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(90);
          cuenta.sacar(1001);
    });
  }

  @Test
  public void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.sacar(1001);
    });
  }

  @Test
  public void ExtraerDinero(){
    cuenta.poner(5);
    cuenta.sacar(1);
    assertEquals(4,cuenta.getSaldo());
  }

  @Test
  public void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));
  }

}