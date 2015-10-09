/* cut-n-paste work from GPL'd phases.c */
#include <time.h>
#include <math.h>

extern time_t JDtoDate(double jd, struct tm *event_date);
extern double DatetoJD(struct tm *event_date);
extern double moonphase(double k, int phi);
extern double moonphasebylunation(int lun, int phi);

