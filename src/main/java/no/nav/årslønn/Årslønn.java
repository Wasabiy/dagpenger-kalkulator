package no.nav.årslønn;

/**
 * Representeren en person sin lønn et kalenderår.
 * Holder på informasjon som hvilket år lønnen tilhører, og selve lønnen det kalenderåret.
 *
 * @author Emil Elton Nilsen
 * @version 1.0
 */
public class Årslønn {

    private final int åretForLønn;
    private final double årslønn;

    public Årslønn(int åretForLønn, double årslønn) {
        if(årslønn < 0) {
            throw new IllegalArgumentException("Årslønn kan ikke være negativ");
        }
        this.åretForLønn = åretForLønn;
        this.årslønn = årslønn;
    }

    /**
     * Henter året som lønnen tilhører.
     * @return året for lønnen.
     */
    public int hentÅretForLønn() {
        return åretForLønn;
    }

    /**
     * Henter årslønnen.
     * @return årslønnen.
     */
    public double hentÅrslønn() {
        return årslønn;
    }

}
