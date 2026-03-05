// frover (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.frover.ui.components;


import java.nio.file.Path;


/** Minimum operations for widgets capable of choosing paths.
  * @author baltasarq
  */
public interface PathChoice {
    int getSelectedIndex();
    Path getPathAt(int row);
}
