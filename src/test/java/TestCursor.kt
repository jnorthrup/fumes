package ed.fumes

import borg.trikeshed.cursor.Cursor
import borg.trikeshed.common.collections.s_
import borg.trikeshed.isam.meta.IOMemento

class TestCursor {

    fun testCursorCreate() {
        //create some ed.fumes.PointRecord3d objects

        val stars=s_[
            PointRecord3d(1UL, "Sol", 0.0, 0.0, 0.0),
            PointRecord3d(2UL, "Alpha Centauri", 4.3, -60.5, 0.0),
            PointRecord3d(3UL, "Barnard's Star", 6.0, 4.0, 0.0),
            PointRecord3d(4UL, "Wolf 359", 7.78, 24.1, 0.0),
            PointRecord3d(5UL, "Lalande 21185", 11.9, 46.0, 0.0),
            PointRecord3d(6UL, "Ross 154", 16.0, 12.0, 0.0),
            PointRecord3d(7UL, "Luyten 726-8A", 18.0, -16.0, 0.0),
            PointRecord3d(8UL, "Epsilon Eridani", 10.5, 44.2, 0.0),
            PointRecord3d(9UL, "Procyon", 11.4, 14.4, 0.0),
            PointRecord3d(10UL, "Sirius", 8.6, -26.7, 0.0),
            PointRecord3d(11UL, "Canopus", -52.7, -82.6, 0.0),
            PointRecord3d(12UL, "Arcturus", 19.2, 16.5, 0.0),
            PointRecord3d(13UL, "Rigel", -8.2, -8.1, 0.0),
            PointRecord3d(14UL, "Capella", 46.0, 95.0, 0.0),
            PointRecord3d(15UL, "Achernar", -57.2, 29.0, 0.0),
            PointRecord3d(16UL, "Altair", -19.5, -87.2, 0.0),
            PointRecord3d(17UL, "Vega", 38.8, 279.2, 0.0),
            PointRecord3d(18UL, "Spica", -11.2, -135.0, 0.0),
        ]

        //create a cursor from the list
        val colnames=s_[ "id", "name", "x", "y", "z"]
        val types=s_[IOMemento.IoLong, IOMemento.IoString, IOMemento.IoFloat, IOMemento.IoFloat, IOMemento.IoFloat]
        val cursor = Cursor.fromList(stars, colnames, types)






