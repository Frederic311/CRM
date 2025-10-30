import { Component, OnInit } from '@angular/core';
import { ScheduleModule } from '@syncfusion/ej2-angular-schedule';
import { HttpClient } from '@angular/common/http';
import {
  EventSettingsModel,
  DayService,
  WeekService,
  WorkWeekService,
  MonthService,
  AgendaService,
} from '@syncfusion/ej2-angular-schedule';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ScheduleModule],
  providers: [DayService, WeekService, WorkWeekService, MonthService, AgendaService],
  template: `
    <ejs-schedule
      width="100%"
      height="550px"
      [selectedDate]="selectedDate"
      [eventSettings]="eventSettings"
    ></ejs-schedule>
  `,
})
export class CalenderComponent implements OnInit {
  public selectedDate: Date = new Date();
  public eventSettings: EventSettingsModel = { dataSource: [] };

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchEvents();
  }

  fetchEvents(): void {
    this.http.get('http://localhost:8089/api/events').subscribe(
      (data: any) => {
        this.eventSettings = {
          dataSource: data.map((event: any) => ({
            Id: event.id,
            Subject: event.name,
            StartTime: new Date(`${event.start}T${event.start_time}`),
            EndTime: new Date(`${event.end}T${event.end_time}`),
          })),
        };

      },
      (error) => {
        console.error('Error fetching events:', error);
      }
    );
  }
}
