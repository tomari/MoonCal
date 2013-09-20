/*
    Calendar with moon phases - JNI bridge
    Copyright (C) 2013  Hisanobu Tomari

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
#include <jni.h>
#include <stdlib.h>
/*#include <android/log.h>*/
#include "fillcal.h"

#define MAX_DAYS_IN_MONTH 42
/*static const char debug_tag[]="MOONNATIVE";*/

static jsize mooncal$minsz(jsize x, jsize y);

static jsize mooncal$minsz(jsize x, jsize y) { return (x<y)?x:y; }

JNIEXPORT void Java_com_example_mooncal_MoonphaseCalculator_calcNative(JNIEnv * env, jobject this,
		jint year, jint month, jdoubleArray phi, jdoubleArray lun) {
	double dblphi[MAX_DAYS_IN_MONTH];
	int intlun[MAX_DAYS_IN_MONTH];
	jsize philen,lunlen;
	jsize phicopysz,luncopysz;
	/* unsigned i; */
	unsigned filledDays;

	filledDays=fillCalendar(year,month,dblphi,intlun);

	philen=(*env)->GetArrayLength(env,phi);
	lunlen=(*env)->GetArrayLength(env,lun);

	phicopysz=mooncal$minsz(filledDays,mooncal$minsz(philen,MAX_DAYS_IN_MONTH));
	luncopysz=mooncal$minsz(filledDays,mooncal$minsz(lunlen,MAX_DAYS_IN_MONTH));
	/*__android_log_print(ANDROID_LOG_DEBUG,debug_tag,"year= %d month= %d phicopysz= %ld luncopysz= %ld filled= %d\n",
			year,month,(long)phicopysz,(long)luncopysz,filledDays);
	for(i=0; i<phicopysz; i++) {
		__android_log_print(ANDROID_LOG_DEBUG,debug_tag,"day= %d phi= %f lun=%d\n",i,(float)dblphi[i],intlun[i]);
	}*/

	(*env)->SetDoubleArrayRegion(env,phi,0,phicopysz,dblphi);
	(*env)->SetIntArrayRegion(env,lun,0,luncopysz,intlun);
}
