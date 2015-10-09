/*
    Calendar with moon phases
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

#include <stdlib.h>
#include <limits.h>
#include <time.h>
#include <string.h>
#include "phases.h"

static time_t timeByMoonphase(unsigned int lun, int phi);
static void lunationEdgesOfTime(const time_t t,
		unsigned int *lower_lun,int *lower_phi,time_t *lower_time,
		unsigned int *upper_lun,int *upper_phi,time_t *upper_time);
static time_t lunphi_next(const unsigned int lun, const int phi,
		unsigned int *next_lun, int * next_phi);

static time_t timeByMoonphase(unsigned int lun, int phi) {
	double JDE=moonphasebylunation(lun,phi);
	time_t res=JDtoDate(JDE,NULL);
	return res;
}

static void lunationEdgesOfTime(const time_t t,
		unsigned int *lower_lun,int *lower_phi,time_t *lower_time,
		unsigned int *upper_lun,int *upper_phi,time_t *upper_time) {
	unsigned int lunphi_lower=0, lunphi_upper, lunphi_mid;
	unsigned int booby_trap=64;
	lunphi_upper=(sizeof(time_t)<8)?5691:16777216; /* time_t still 32-bit long */
	time_t time_mid,time_upper=t,time_lower=t;
	do {
		lunphi_mid=(lunphi_lower+lunphi_upper)>>1;
		time_mid=timeByMoonphase((lunphi_mid>>2),(lunphi_mid&3));
		if(difftime(time_mid,t)>=0.) {
			lunphi_upper=lunphi_mid;
			time_upper=time_mid;
		} else {
			lunphi_lower=lunphi_mid;
			time_lower=time_mid;
		}
	} while ((lunphi_upper-lunphi_lower!=1) && booby_trap--);
	*lower_lun=(lunphi_lower>>2);
	*lower_phi=(lunphi_lower&3);
	*upper_lun=(lunphi_upper>>2);
	*upper_phi=(lunphi_upper&3);
	*lower_time=time_lower;
	*upper_time=time_upper;
}

static time_t lunphi_next(const unsigned int lun, const int phi,
	unsigned int *next_lun, int * next_phi) {
	unsigned int lunphi_next=((lun<<2)|(phi&3))+1;
	*next_lun=(lunphi_next>>2);
	*next_phi=lunphi_next&3;
	return timeByMoonphase(*next_lun,*next_phi);
}

extern unsigned fillCalendar(const int year, const int month, double *lunations,
		int *quarterDay) {
	struct tm man_readable_time;
	time_t mac_time,lower_t,upper_t;
	unsigned int lower_lun,upper_lun;
	int lower_phi,upper_phi;
	unsigned fillingDay=0;
	unsigned booby_trap=42;

	memset(&man_readable_time,0,sizeof(man_readable_time));
	man_readable_time.tm_mday=1;
	man_readable_time.tm_mon=month-1;
	man_readable_time.tm_year=year-1900;
	mac_time=mktime(&man_readable_time);
	lunationEdgesOfTime(mac_time,
			    &lower_lun,&lower_phi,&lower_t,
			    &upper_lun,&upper_phi,&upper_t);

	do {
		double diff_x,diff_y,diff_z;
		double lunation_r,lunation_fraction;
		diff_x=difftime(upper_t,lower_t);
		diff_y=difftime(mac_time,lower_t);
		lunation_fraction=diff_y/diff_x;
		if(lunation_fraction<1.) {
			diff_z=difftime(upper_t,mac_time);
			lunation_r=lunation_fraction+(double)lower_phi;
			quarterDay[fillingDay]=(diff_z<86400.)?(double)upper_phi:-1;
			lunations[fillingDay]=lunation_r;
			fillingDay++;
			man_readable_time.tm_mday++;
			mac_time=mktime(&man_readable_time);
		} else {
			lower_lun=upper_lun;
			lower_phi=upper_phi;
			lower_t=upper_t;
			upper_t=lunphi_next(lower_lun,lower_phi,&upper_lun,&upper_phi);
		}
	} while((man_readable_time.tm_mon==(month-1)) && (booby_trap--));
	return fillingDay;
}

