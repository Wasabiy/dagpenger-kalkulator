package dagpenger;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import no.nav.dagpenger.BeregningsMetode;
import no.nav.dagpenger.DagpengerKalkulator;
import no.nav.saksbehandler.Sak;
import no.nav.årslønn.Årslønn;

public class DagpengerKalkulatorTester {

    @Test
    public void testSkalHaRettigheterTilDagpengerUtifraSisteTreÅrslønner()  {
        DagpengerKalkulator dagpengerKalkulator = new DagpengerKalkulator();
        List<Årslønn> årslønner = new ArrayList<>();
        Sak sak = new Sak(1L, "Test", 123456789);
        sak.setÅrslønner(årslønner);
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2023, 445000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2025, 465000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2024, 300000));
        assertTrue(dagpengerKalkulator.harRettigheterTilDagpenger(sak));
    }

    @Test
    public void testSkalHaRetigheterTilDagpengerSisteÅrslønn() {
        DagpengerKalkulator dagpengerKalkulator = new DagpengerKalkulator();
        List<Årslønn> årslønner = new ArrayList<>();
        Sak sak = new Sak(1L, "Test", 123456789);
        sak.setÅrslønner(årslønner);
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2023, 0));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2024, 0));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2025, 467000));
        assertTrue(dagpengerKalkulator.harRettigheterTilDagpenger(sak));
    }

    @Test
    public void testSkalIkkeHaRettigheterTilDagpengerSisteTreÅrslønner()  {
        DagpengerKalkulator dagpengerKalkulator = new DagpengerKalkulator();
        List<Årslønn> årslønner = new ArrayList<>();
        Sak sak = new Sak(1L, "Test", 123456789);
        sak.setÅrslønner(årslønner);
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2023, 44000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2025, 52000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2024, 100000));
        assertFalse(dagpengerKalkulator.harRettigheterTilDagpenger(sak));
    }

    @Test
    public void testSkalIkkeHaRettigheterTilDagpengerSisteÅrslønn()  {
        DagpengerKalkulator dagpengerKalkulator = new DagpengerKalkulator();
        List<Årslønn> årslønner = new ArrayList<>();
        Sak sak = new Sak(1L, "Test", 123456789);
        sak.setÅrslønner(årslønner);
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2023, 0));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2025, 130000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2024, 0));
        assertFalse(dagpengerKalkulator.harRettigheterTilDagpenger(sak));
    }

    @Test
    public void testBeregningsMetodeBlirSattTilSisteÅrslønn() {
        DagpengerKalkulator dagpengerKalkulator = new DagpengerKalkulator();
        List<Årslønn> årslønner = new ArrayList<>();
        Sak sak = new Sak(1L, "Test", 123456789);
        sak.setÅrslønner(årslønner);
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2025, 550000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2023, 110000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2024, 24000));
        assertEquals(BeregningsMetode.SISTE_ÅRSLØNN, dagpengerKalkulator.velgBeregningsMetode(årslønner));
    }

    @Test
    public void testBeregningsMetodeBlirSattTilMaksÅrslønnGrunnbeløp() {
        DagpengerKalkulator dagpengerKalkulator = new DagpengerKalkulator();
        List<Årslønn> årslønner = new ArrayList<>();
        Sak sak = new Sak(1L, "Test", 123456789);
        sak.setÅrslønner(årslønner);
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2025, 830000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2023, 110000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2024, 24000));
        assertEquals(BeregningsMetode.MAKS_ÅRLIG_DAGPENGERGRUNNLAG, dagpengerKalkulator.velgBeregningsMetode(årslønner));
    }

    @Test
    public void testBeregningsMetodeBlirSattTilGjennomsnittetAvTreÅr() {
        DagpengerKalkulator dagpengerKalkulator = new DagpengerKalkulator();
        List<Årslønn> årslønner = new ArrayList<>();
        Sak sak = new Sak(1L, "Test", 123456789);
        sak.setÅrslønner(årslønner);
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2025, 330000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2023, 400000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2024, 334000));
        assertEquals(BeregningsMetode.GJENNOMSNITTET_AV_TRE_ÅR, dagpengerKalkulator.velgBeregningsMetode(årslønner));
    }

    @Test
    public void testDagsatsKalkulertUtifraSisteÅrslønn() {
        DagpengerKalkulator dagpengerKalkulator = new DagpengerKalkulator();
        List<Årslønn> årslønner = new ArrayList<>();
        Sak sak = new Sak(1L, "Test", 123456789);
        sak.setÅrslønner(årslønner);
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2025, 550000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2023, 110000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2024, 24000));
        assertEquals(2116, dagpengerKalkulator.kalkulerDagsats(sak));
    }

    @Test
    public void testDagsatsKalkulertUtifraMaksÅrligGrunnbeløp() {
        DagpengerKalkulator dagpengerKalkulator = new DagpengerKalkulator();
        List<Årslønn> årslønner = new ArrayList<>();
        Sak sak = new Sak(1L, "Test", 123456789);
        sak.setÅrslønner(årslønner);
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2025, 830000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2024, 24000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2023, 110000));
        assertEquals(3004, dagpengerKalkulator.kalkulerDagsats(sak));
    }

    @Test
    public void testDagsatsKalkulertUtifraTreÅrsGjennomsnitt() {
        DagpengerKalkulator dagpengerKalkulator = new DagpengerKalkulator();
        List<Årslønn> årslønner = new ArrayList<>();
        Sak sak = new Sak(1L, "Test", 123456789);
        sak.setÅrslønner(årslønner);
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2025, 330000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2024, 334000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2023, 400000));
        assertEquals(1365, dagpengerKalkulator.kalkulerDagsats(sak));
    }

    @Test
    public void testDagsatsKalkulertIkkeRettPåDagpenger() {
        DagpengerKalkulator dagpengerKalkulator = new DagpengerKalkulator();
        List<Årslønn> årslønner = new ArrayList<>();
        Sak sak = new Sak(1L, "Test", 123456789);
        sak.setÅrslønner(årslønner);
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2025, 80000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2024, 100000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2023, 70000));
        assertEquals(0, dagpengerKalkulator.kalkulerDagsats(sak));
    }
}
