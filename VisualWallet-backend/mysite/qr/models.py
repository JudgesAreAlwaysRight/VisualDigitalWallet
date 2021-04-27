import datetime
from django.db import models
from django.utils import timezone
# Create your models here.


class SKInfo(models.Model):
    secretKeyHash = models.CharField(max_length=512)
    coeK = models.IntegerField()
    coeN = models.IntegerField()
    carry0 = models.BinaryField()
    carry1 = models.BinaryField(blank=True)
    carry2 = models.BinaryField(blank=True)
    carry3 = models.BinaryField(blank=True)
    carry4 = models.BinaryField(blank=True)
    length = models.IntegerField()
    width = models.IntegerField()
    c1 = models.IntegerField()
    c2 = models.IntegerField()
    c3 = models.IntegerField()
