# Generated by Django 3.2.6 on 2023-10-28 08:42

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('entry', '0002_auto_20231027_1301'),
    ]

    operations = [
        migrations.AlterField(
            model_name='symbol',
            name='category',
            field=models.IntegerField(),
        ),
        migrations.DeleteModel(
            name='Category',
        ),
    ]
