# Generated by Django 3.2.6 on 2023-12-09 05:31

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('setup', '0002_setting_updated_at'),
    ]

    operations = [
        migrations.AddField(
            model_name='setting',
            name='auto_backup',
            field=models.IntegerField(choices=[(0, 'non-auto'), (1, 'auto')], default=1),
        ),
        migrations.AlterField(
            model_name='setting',
            name='default_menu',
            field=models.IntegerField(choices=[(0, 'tts'), (1, 'symbol')], default=0),
        ),
    ]