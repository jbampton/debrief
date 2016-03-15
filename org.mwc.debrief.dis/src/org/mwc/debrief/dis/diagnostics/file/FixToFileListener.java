package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISFixListener;

public class FixToFileListener extends CoreFileListener implements
    IDISFixListener
{

  public FixToFileListener(String root, boolean toFile, boolean toScreen)
  {
    super(root, toFile, toScreen, "fix",
        "time, exerciseId, name,force, kind, domain, category, isHighlighted, dLat, dLong, depth, courseDegs, speedMS, damage");
  }

  @Override
  public void add(long time, short exerciseId, long id, String eName,
      short force, short kind, short domain, short category, boolean isHighlighted, double dLat, double dLong, double depth, double courseRads, double speedMS, int damage)
  {
    // create the line
    StringBuffer out = new StringBuffer();
    out.append(time);
    out.append(", ");
    out.append(id);
    out.append(", ");
    out.append(eName);
    out.append(", ");
    out.append(force);
    out.append(", ");
    out.append(kind);
    out.append(", ");
    out.append(domain);
    out.append(", ");
    out.append(category);
    out.append(", ");
    out.append(isHighlighted);
    out.append(", ");
    out.append(dLat);
    out.append(", ");
    out.append(dLong);
    out.append(", ");
    out.append(depth);
    out.append(", ");
    out.append(Math.toDegrees(courseRads));
    out.append(", ");
    out.append(speedMS);
    out.append(", ");
    out.append(damage);
    out.append(LINE_BREAK);

    // done, write it
    write(out.toString());
  }

}