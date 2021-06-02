#include <iostream>
#include <string>
using namespace std;
// ����androidID 8λstring(carrier���߱�����ȡ)��
// �ܹ����ٸ��ִ�ͼn,�ڼ����ִ�ͼsplitID(�ܴ�carrier���)
// �ִ��õľ���s0,s1(int**)(д�������ݴ���)
// ��Ҫ���ľ���toDetect(int**)
bool detect(string androidID,int n, int splitID, int** s0, int** s1, int** toDetect)
{
	bool isCheater = false;
	//��������
	int queue[8];
	for (int i = 0; i < 8; i++)
	{
		queue[i] = (int(androidID[i]) % n);
	}
	//��n(0��ʼ)�ŷִ�ͼ����λ���ڵ�n*8~n*8+7λ
	int start = splitID * 8;
	int end = start + 7;
	//�������Ͷ�
	int m = sizeof(s0[0]) / sizeof(s0[0][0]);
	int edge = sqrt(m);
	//�������ڱȽϵľ���comp0,comp1
	/*
	* ��comp0Ϊ����comp0[8][edge][edge]
	* ��һ��index��Ӧn*8~n*8+7
	* �ڶ���index��Ӧ���ͺ���������꣬�������Ǻ�����
	*/
	int*** comp0 = new int** [8];
	int*** comp1 = new int** [8];
	for (int i = 0; i < 8; i++)
	{
		comp0[i] = new int* [edge];
		comp1[i] = new int* [edge];
		for (int j = 0; j < edge; j++) 
		{
			comp0[i][j] = new int[edge];
			comp1[i][j] = new int[edge];
		}
	}
	for (int x = 0; x < 8; x++)
	{
		for (int y = 0; y < edge; y++)
		{
			for (int z = 0; z < edge; z++)
			{
				comp0[x][y][z] = s0[queue[x]][y * edge + z];
				comp1[x][y][z] = s1[queue[x]][y * edge + z];
			}
		}
	}
	//�ҵ���Ӧλ�ò��Ƚ�
	int startX = start / 16;
	int startY = start % 16;
	for (int i = 0; i < 8; i++)
	{
		for (int x = 0; x < edge; x++)
		{
			for (int y = 0; y < edge; y++)
			{
				if (toDetect[startX * edge + x][startY * edge + y] != comp0[i][x][y]) 
				{
					if (toDetect[startX * edge + x][startY * edge + y] != comp1[i][x][y])
					{
						isCheater = true;
					}
				}
			}
		}
	}
	
	return isCheater;
}
