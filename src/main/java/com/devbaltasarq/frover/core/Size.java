// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.core;

import java.math.BigInteger;


/** Represents a given size.
  * @author baltasarq
  */
public class Size {
    public enum Unit {
        B(                                 BigInteger.ONE ),    // 1B
        K(                       BigInteger.valueOf( 1024 ) ),  // 1K = 1024 * 1B
        M(                  BigInteger.valueOf( 1_048_576 ) ),  // 1M = 1024 * 1K
        G(              BigInteger.valueOf( 1_073_741_824 ) ),  // 1G = 1024 * 1M
        T(                new BigInteger( "1099511627776" ) ), // 1T = 1024 * 1G
        P(             new BigInteger( "1125899906842624" ) ), // 1P = 1024 * 1T
        E(          new BigInteger( "1152921504606846976" ) ), // 1E = 1024 * 1P
        Z(       new BigInteger( "1180591620717411303424" ) ); // 1Z = 1024 * 1P
        
        Unit(BigInteger q)
        {
            this.quantity = q;
        }
        
        public BigInteger get()
        {
            return this.quantity;
        }
        
        private final BigInteger quantity;
    }
    
    public Size(long size)
    {
        this.size = size;
        this.determineUnit();
    }
    
    private void determineUnit()
    {
        final Unit[] UNIT = Unit.values();
        this.unit = Unit.B;
        
        for(int i = 1; i < UNIT.length; ++i) {
            if ( this.size < UNIT[ i ].get().doubleValue() ) {
                this.unit = UNIT[ i - 1 ];
                break;
            }
        }
        
        return;
    }
    
    /** @return the size in bytes. */
    public double get()
    {
        return this.size;
    }
    
    /** @return the units for this size. */
    public Unit getUnit()
    {
        return this.unit;
    }
    
    @Override
    public String toString()
    {
        double unitSize = this.get() / this.getUnit().get().doubleValue();
        
        return String.format( "%7.2f%s", unitSize, this.getUnit().toString() );
    }
    
    private final double size;
    private Unit unit;
}
